package net;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sql.DatabaseServer;
import util.DatabaseServerConnectionInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class Server {
    private final Log LOG = LogFactory.getLog(Server.class);

    private  static ArrayList<Connection> connections;

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
        System.out.println("Start serwera..");
        System.out.println("Łączenie z bazą..");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        DatabaseServerConnectionInfo connectionInfo =
                new DatabaseServerConnectionInfo("localhost", "1521");
        connectionInfo.setUsername("LORKANO");
        connectionInfo.setPassword("oracle");

        DatabaseServer db = DatabaseServer.getInstance();
        try {
            java.sql.Connection connection = db.connect(connectionInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Tworzenie i wybór bazy database
        //db.createDatabaseIfDoesNotExists();

        //create tables
        db.createTablesIfDoesNotExists();

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
                    LOG.info("Ilość aktywnych połączeń: "+connections.size());
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

    public static void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public static ArrayList<Connection> getConnections() {
        return connections;
    }

    public static void setConnections(ArrayList<Connection> connections) {
        Server.connections = connections;
    }
}
