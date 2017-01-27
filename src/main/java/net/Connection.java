package net;

import util.DataPackage;
import util.InputHandler;
import util.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    private boolean logged = false; //status zalogowania
    private Socket socket;
    private Thread thread;
    private User user = null;
    private InputHandler inputHandler = new InputHandler();

    public Connection(Socket socket) {
        this.socket = socket;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ObjectOutputStream output       = null;
                ObjectInputStream  input        = null;
                DataPackage        dataPackage  = null;

                try {
                    output = new ObjectOutputStream(socket.getOutputStream());
                    input  = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while(true) {
                    try {
                        dataPackage = (DataPackage) input.readObject();
                        if (dataPackage == null) {
                            continue;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    dataPackage = inputHandler.handle(dataPackage);

                    if (dataPackage == null) {
                        System.out.println("Nieobsługiwane zapytanie dla serwera");
                    }

                    try {
                        output.writeObject(dataPackage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isLogged() {
        return logged;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
