package net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private final Log LOG = LogFactory.getLog(Server.class);
    private ArrayList<Connection> connections;
    private ServerSocket serverSocket;
    private int port;

    private final int DEFAULT_PORT = 4000;

    // Wątek przyjmujący połączenia oraz tworzący wątki obsługi
    private Thread mainThread;

    // Jeśli true, mainThread będzie kontynuował przyjmowanie połączeń
    private boolean running;

    public Server() {
        super();
    }

    public void run(int p) {
        System.out.println("Starting up the server..");

        //inicjacja pól
        port = p;
        connections = new ArrayList<Connection>();
        running = true;

        //tworzenie socketu serwera
        serverSocket = createServerSocket(port);
        System.out.println("Started server at port: " + port);

        //tworzenie wątku przyjmującego połączenia oraz tworzącego wątki obsługujące klientów
        mainThread = createMainThread();
        mainThread.start();
    }

    private void stopServer() {
        disconnectClients();
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.fatal("Error while closing server socket");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private void disconnectClients() {
        //TODO: DISCONNECT ALL CLIENTS
    }

    public static void main(String[] args) {

        //Obsługa portu przez parametr
        Server server = new Server();
        server.run(4000);


    }

    public int getPort(String[] args) {
        if (args.length == 0) {
            return DEFAULT_PORT;
        } else {
            try {
                int tmp = Integer.parseInt(args[0]);
                return tmp;
            } catch (NumberFormatException e) {
                LOG.error("Error: Parameter has to be number, switching to default port.");
                return DEFAULT_PORT;
            }
        }
    }

    private ServerSocket createServerSocket(int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            LOG.fatal("Error while creating server socket at port: " + port);
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    private Thread createMainThread() {
        return new Thread(new Runnable() {
            public void run() {
                while (running) {
                    Socket clientSocket;
                    Connection c;

                    //przyjmowanie połączenia
                    try {
                        clientSocket = serverSocket.accept();
                        LOG.info("Połączono z "+clientSocket.toString());
                    } catch (IOException e) {
                        if (serverSocket.isClosed()) {
                            System.out.println("net.Server socket has been closed, finishing mainThread.");
                            running = false;
                            continue;
                        }
                        LOG.error("Error while accepting connection.");
                        continue;
                    }

                    //Dodawanie połączenia do listy
                    c = new Connection(clientSocket);

                    if (!connections.add(c)) {
                        LOG.error("Error: cant add connection into list");
                        continue;
                    }
                }

                //Kończenie serwera po zakończeniu pętli
                stopServer();
                System.out.println("End of server");
            }
        });
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getDefaultPort() {
        return DEFAULT_PORT;
    }
}
