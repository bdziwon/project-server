package util;

import net.Connection;
import net.Server;
import sql.DatabaseServer;

import java.util.ArrayList;
import java.util.Iterator;


public class InputHandler {

    private Connection connection;

    public InputHandler(Connection connection) {
        this.connection = connection;
    }

    public DataPackage handle(DataPackage dataPackage) {

        switch (dataPackage.getDetails()) {
            case "login" :
                dataPackage = login(dataPackage);
                return dataPackage; // Return to zawsze wynik dla klienta

            case "register" :
                dataPackage = register(dataPackage);
                return dataPackage;

            case "disconnect" :
                //TODO: usunąć Connection z listy z klasy Server
                this.getConnection().setActive(false);
                Server.removeConnection(this.getConnection());
                dataPackage.setDetails("disconnected");
                return  dataPackage;

            case "insert" :
                //Przykład: powinien zwrócić zinsertowany obiekt w datapackage, to wypełni jego pole id i zwróci do klienta
                Object object = dataPackage.getObject();
                object = insert(object);
                dataPackage.setObject(object);
                return dataPackage;

            default:
                return null;
        }

    }

    /**
     * to stworzy dowolny projekt, błąd lub użytkownika, wystarczy żeby rozróżnić że to insert
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return zwraca obiekt z id z bazy, nie dodaje issue i user do projektu jeśli są na liście,
     * to robi dopiero update dla projektu
     */
    private Object insert(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        object = db.insert(object);

        return object;
    }

        /**
     * Aktualizuje, jeśli aktualizujemy projekt to stworzą się automatycznie nieistniejący użytkownicy i błędy o
     * oraz się zaktualizują
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return Object ilość zmian w projekcie
     */
    private Object update(Object object) {
        //update
        DatabaseServer db = DatabaseServer.getInstance();
        return db.update(object);
    }

    /**
     * Usuwanie z bazy po id w obiekcie
     * Jeśli usuwamy projekt usunie też jego błędy, użytkownicy zostają
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return ilość zmian w bazie, jak zero to błąd
     */
    private Object delete(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.delete(object);
    }

    /**
     * Pobiera dane z bazy do obiektu
     * Jeśli to projekt, pobierze także wszystkie błędy i użytkowników do list
     * @param object Musi zawierać id obiektu i być odpowiednią klasą
     * @return Obiekt reprezentujący dane z bazy
     */
    private Object select(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.select(object);
    }
    //TODO: zapis projektu (plików) na serwerze, mogą być np zapisywane w folderach o nazwie..
    // jak tytuł projektu zamiast ścieżek jakichś

    //TODO: pobieranie plików z serwera
    //TODO: String w żadnym obiekcie nie może zawierac apostrofu '

    private DataPackage login (DataPackage dataPackage) {

        DatabaseServer db = DatabaseServer.getInstance();
        Credentials credentials = (Credentials) dataPackage.getObject();
        User user = db.select(credentials);

        if (user == null) {
            dataPackage.setDetails("user not found");
            return dataPackage;
        }

        //sprawdzanie czy jest zalogowany
        ArrayList<Connection> connections = Server.getConnections();
        Iterator i = connections.iterator();
        boolean logged = false;

        while (i.hasNext()) {
            Connection c = (Connection) i.next();
            if (c.getLoggedUserId() == user.getId()) {
                logged = true;
                break;
            }
        }

        if (logged) {
            //jeśli jest już zalogowany
            dataPackage.setDetails("already logged");
            dataPackage.setObject(user);

            return dataPackage;
        }

        //jeśli nie jest jeszcze zalogowany
        this.getConnection().setLogged(true);
        this.getConnection().setLoggedUserId(user.getId());

        dataPackage.setDetails("logged");
        dataPackage.setObject(user);

        return dataPackage;
    }

    private DataPackage register(DataPackage dataPackage) {

        DatabaseServer db = DatabaseServer.getInstance();
        ArrayList<Object> params = (ArrayList<Object>) dataPackage.getObject();

        if (params.size() < 2) {
            dataPackage.setDetails("expected two params");
            return dataPackage;
        }
        Credentials credentials = (Credentials) params.get(0);
        if (db.select(credentials) == null) {
            System.out.println("wstawiam");
            User user = (User) params.get(1);
            user = (User) insert(user);
            if (!db.insertCredentials(user, credentials)) {
                dataPackage.setDetails("login/password error");
                return dataPackage;
            }
            dataPackage.setDetails("registered");
            dataPackage.setObject(user);
            return dataPackage;
        }
        dataPackage.setDetails("user exists");
        return dataPackage;
    }


    private Object logout (Object object) {
        //TODO: Wylogowywanie, ustawia odpowiednie connection.logged na false oraz usuwa connection.user na null
        return object;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
