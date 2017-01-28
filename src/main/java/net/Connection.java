package net;

import util.DataPackage;
import util.InputHandler;
import util.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {

    /** Status zalogowania, ustawiamy na true jeśli jest zalogowany, oraz ustawiamy usera **/
    private boolean logged   = false;

    /** ID zalogowanego użytkownika z bazy **/
    private int loggedUserId = -1;

    /** Ustawiamy na false aby zakończyć połączenie **/
    private boolean active = true;

    /** Socket do operacji **/
    private Socket socket;

    private InputHandler inputHandler = new InputHandler(this);

    public Connection(Socket socket) {

        this.socket = socket;

        Thread thread = new Thread(new Runnable() {
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

                while(active) {
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

    public int getLoggedUserId() {
        return loggedUserId;
    }

    public void setLoggedUserId(int loggedUserId) {
        this.loggedUserId = loggedUserId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
