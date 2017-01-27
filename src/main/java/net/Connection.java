package net;

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
    private InputHandler inputHandler;
    public Connection(Socket socket) {
        this.socket = socket;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO: wątek korzystający z InputHandlera do wymiany z klientem
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream input  = new ObjectInputStream(socket.getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
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
