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

            case "logout" :
                dataPackage = logout(dataPackage);
                return dataPackage;
            case "register" :
                dataPackage = register(dataPackage);
                return dataPackage;

            case "disconnect" :
                dataPackage = disconnect(dataPackage);
                return dataPackage;

            case "select username" :
                dataPackage = selectUserByLogin(dataPackage);
                return dataPackage;

            case "insert" :
                Object object = dataPackage.getObject();
                object = insert(object);
                dataPackage.setObject(object);
                return dataPackage;

            case "update" :
                dataPackage = update(dataPackage);
                return dataPackage;

            case "delete" :
                delete(dataPackage);
                return dataPackage;

            case "list projects" :
                dataPackage = getProjectList(dataPackage);
                return dataPackage;

            default:
                return null;
        }

    }

    private DataPackage getProjectList(DataPackage dataPackage) {
        DatabaseServer      db          = DatabaseServer.getInstance();
        ArrayList<Project>  projects    = db.getProjectList(connection.getLoggedUser());

        dataPackage.setDetails("projects");
        dataPackage.setObject(projects);
        return  dataPackage;
    }

    private DataPackage selectUserByLogin(DataPackage dataPackage) {
        DatabaseServer  db      = DatabaseServer.getInstance();
        String          login   = (String) dataPackage.getObject();
        User user = db.selectUserByLogin(login);
        dataPackage.setObject(user);
        return dataPackage;
    }

    private DataPackage logout(DataPackage dataPackage) {
        this.getConnection().setLoggedUser(new User());
        dataPackage.setDetails("skip sending");
        return dataPackage;
    }

    private DataPackage disconnect(DataPackage dataPackage) {
        dataPackage = logout(dataPackage);
        System.out.println("Rozłączono z "+this.getConnection().getSocket().toString());
        this.getConnection().setActive(false);
        Server.removeConnection(this.getConnection());
        dataPackage.setDetails("skip sending");
        System.out.println("Ilość aktywnych połączeń: "+Server.getConnections().size());
        return  dataPackage;
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
     * @return Object ilość zmian w projekcie
     */
    private DataPackage update(DataPackage dataPackage) {
        //update
        DatabaseServer db = DatabaseServer.getInstance();
        int changes = db.update(dataPackage.getObject());
        if (changes > 0) {
            dataPackage.setDetails("added");
            return dataPackage;
        }
        dataPackage.setDetails("error");
        return dataPackage;
    }

    /**
     * Usuwanie z bazy po id w obiekcie
     * Jeśli usuwamy projekt usunie też jego błędy, użytkownicy zostają
     * @return ilość zmian w bazie, jak zero to błąd
     */
    private Object delete(DataPackage dataPackage) {
        DatabaseServer db = DatabaseServer.getInstance();
        int changes = db.delete(dataPackage.getObject());
        if (changes > 0) {
            dataPackage.setDetails("deleted");
        }
        return dataPackage;
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
        User user = db.select(credentials,false);

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
            if (c.getLoggedUser().getId() == user.getId()) {
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
        this.getConnection().setLoggedUser(user);

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
        if (db.select(credentials, true) == null) {
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
