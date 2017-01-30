package sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import util.*;
import util.interfaces.DatabaseSqlInterface;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Singleton do obsługi bazy danych
 * Obsługa:
 * Wywołujemy getInstance(), następnie pierwszy raz musimy się połączyć za pomocą connect
 * oraz stworzyć bazy za pomocą createDatabaseIfDoesNotExists i createTablesIfDoesNotExists.
 * potem możemy wywoływać resztę metod
 */

public class DatabaseServer {

    //TODO: Zrobić selecta który zwraca listę wszystkich projektów ale bez ich issues i users, koniecznie z id,
    // można skorzystać z metody select aby pobierać zawartość pojedynczych

    private static DatabaseServer db = null;
    private Connection connection = null;
    private DatabaseServerConnectionInfo connectionInfo = null;
    private final Log LOG = LogFactory.getLog(DatabaseServer.class);


    private DatabaseServer() {
    }

    public static DatabaseServer getInstance() {
        if (db == null) {
            db = new DatabaseServer();
            return db;
        } else {
            return db;
        }
    }

    /**
     * łączenie z bazą
     *
     * @param connectionInfo musi zawierać poprawną
     *                       nazwę użytkownika, hasło, nazwę hosta, i port
     * @return instancja połączenia {@link Connection}
     * @throws SQLException SQLException przy złych hasłach itd
     */
    public Connection connect(DatabaseServerConnectionInfo connectionInfo) throws IllegalArgumentException, SQLException {
        if (this.connection != null) {
            LOG.info("Połączenie nie udane, już połączono");
            return this.connection;
        }

        Connection connection;
        String link;
        String username = connectionInfo.getUsername();
        String password = connectionInfo.getPassword();

        //sprawdzanie czy link został poprawnie stworzony
        try {
            link = connectionInfo.makeDbLink();
        } catch (IllegalArgumentException e) {
            throw e;
        }

        try {
            if (password == null) {
                password = "";
                connectionInfo.setPassword("");
            }

            //łączenie z mysql
            if (username == null) {
                LOG.info("username jest nullem, próba łączenia bez loginu i hasła");
                connection = DriverManager.getConnection(link);
            } else {
                LOG.info("łączenie za pomocą hasła");
                connection = DriverManager.getConnection(link, username, password);
            }
        } catch (SQLException e) {
            throw e;
        }

        //nie chcemy nulla
        if (connection == null) {
            LOG.error("Połączenie nieudane, zwrócono null");
            return connection;
        }

        LOG.info("Połaczenie udane!");
        this.connectionInfo = connectionInfo;
        this.connection = connection;
        return connection;
    }

    /**
     * Tworzy bazę danych "database" i wybiera ją w {@link Connection}
     */
    public void createDatabaseIfDoesNotExists() {
        String sql =
                "CREATE DATABASE `" + "database" + "` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";

        try {
            connection.createStatement().executeUpdate(sql);
            connection.setCatalog("database");
        } catch (SQLException e) {
            if (e.toString().contains("database exists")) {
                //Obsługiwany wyjątek

                LOG.info("Baza już istnieje");
                try {
                    connection.setCatalog("database");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            } else {
                //Nieoczekiwany wyjątek

                e.printStackTrace();
            }
        }
    }

    /**
     * tworzy tabele w bazie "database"
     */
    @SuppressWarnings("SqlResolve")
    public void createTablesIfDoesNotExists() {
        try {
            String sql =
                    "CREATE TABLE IF NOT EXISTS user (" +
                            "id       INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "name     VARCHAR(50)                         NOT NULL DEFAULT 'pusto'," +
                            "surname  VARCHAR(50)                         NOT NULL DEFAULT 'pusto'," +
                            "jobTitle ENUM('PROGRAMISTA','TESTER','ADMINISTRATOR') NOT NULL," +
                            "login    VARCHAR(50)                         NOT NULL DEFAULT  'pusto'," +
                            "password VARCHAR(50)                         NOT NULL DEFAULT  'pusto')";

            connection.createStatement().executeUpdate(sql);


            sql =
                    "CREATE TABLE IF NOT EXISTS project (" +
                            "id          INT(5)                           NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "title       VARCHAR(50)                      NOT NULL DEFAULT 'Brak tytułu'," +
                            "description VARCHAR(150)                     NOT NULL DEFAULT 'Brak opisu')";
            //bez pól do błędów i użytkowników, potrzebne osobne tabele project_user, project_issue

            connection.createStatement().executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS issue (" +
                            "id          INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project  INT(5)                              NOT NULL," +
                            "title       VARCHAR(50)                         NOT NULL DEFAULT 'Brak tytułu'," +
                            "description VARCHAR(150)                        NOT NULL DEFAULT 'Brak opisu'," +
                            "priority    ENUM('ZWYKŁY','NORMALNY', 'WYSOKI') NOT NULL, " +
                            "CONSTRAINT project_fk FOREIGN KEY (id_project) REFERENCES project(id) " +
                            "ON DELETE CASCADE)";
            //W takim wierszu domyślnym jest pierwsza wartość enuma

            connection.createStatement().executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS project_user(" +
                            "id         INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project INT(5)                              NOT NULL," +
                            "id_user    INT(5)                              NOT NULL," +
                            "CONSTRAINT project_fk_2 FOREIGN KEY (id_project) REFERENCES project(id)" +
                            "ON DELETE CASCADE," +
                            "CONSTRAINT user_fk      FOREIGN KEY (id_user)    REFERENCES user(id) " +
                            "ON DELETE CASCADE)";

            connection.createStatement().executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wstawia obiekt do odpowiedniej tabeli
     * UWAGA: INSERT nie wrzuca błędów i użytkowników projektów, to robi UPDATE.
     * Aby wrzucić cały projekt robimy najpierw insert, później update
     * Insert wrzuca projekt z ustawionymi własnymi parametrami
     *
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}
     * @return Obiekt z wypełnionym polem ID odpowiadającym polu w bazie
     */
    public Object insert(Object object) throws IllegalArgumentException {

        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }

        //Dla każdego obiektu Issue, Project, user zawsze stworzy poprawnego inserta
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeInsertSql();
        try {
            Statement statement = connection.createStatement();
            int changes = statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            if (changes == 0) {
                LOG.error("Brak zmian w tabeli");
            } else {
                LOG.info(changes + " zmian w tabeli");
                ResultSet keys = statement.getGeneratedKeys();
                if (keys.next()) {
                    sqlInterface.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * Usuwa obiekt z tabeli sprawdzając id
     *
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}  <br>
     *               ID obiektu musi być większe od -1
     * @return int z liczbą usuniętych wierszy
     * @throws IllegalArgumentException Wyjątek gdy damy obiekt niewspieranej klasy
     */
    public int delete(Object object) throws IllegalArgumentException {

        //sprawdzanie typu klasy
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }
        //wysyłanie zapytania
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeDeleteSql();
        try {
            int changes = connection.createStatement().executeUpdate(sql);
            LOG.info("Usuniętych pozycji: " + changes);
            return changes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Pobiera Usera z bazy na podstawie loginu i hasła
     * @param credentials obiekt zawierający login i hasło
     * @return zwraca Usera o loginie i haśle lub null jeśli nie znaleziono
     */
    public User select(Credentials credentials, boolean onlyLogin) {

        ResultSet   resultSet   = null;
        String      login       = credentials.getLogin();
        String      password    = credentials.getPassword();
        User        user        = new User();
        String      sql;

        if (onlyLogin)  {
            sql =
                "SELECT * FROM user " +
                        "WHERE login = '"+login+"'";
        } else {
            sql =
                "SELECT * FROM user " +
                        "WHERE login = '" + login + "' AND " +
                        "password = '" + password + "'";
        }
        System.out.println(sql);

        try {
            resultSet = connection.createStatement().executeQuery(sql);
            if (resultSet.next()) {
                user = user.resultSetToObject(resultSet);
                return user;
            }
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean insertCredentials(User user, Credentials credentials) throws IllegalArgumentException {
        DatabaseServer db   = DatabaseServer.getInstance();
        String sql          = "";
        String login        = credentials.getLogin();
        String password     = credentials.getPassword();

        if (user.getId() == -1) {
            throw new IllegalArgumentException("user id is -1");
        }
        sql =
                "UPDATE user " +
                        "SET " +
                        "login = '" + login + "', " +
                        "password = '" + password + "' " +
                        "WHERE id = " + user.getId();
        try {
            int changes = connection.createStatement().executeUpdate(sql);
            if (changes > 0) {
                return  true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @SuppressWarnings("SqlResolve")
    public Object select(Object object) throws IllegalArgumentException {

        //sprawdzanie typu klasy
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }

        //wysyłanie zapytania
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeSelectSql();
        Object result = null;
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(sql);
            if (resultSet.next()) {
                result = sqlInterface.resultSetToObject(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (result == null || c != Project.class) {
            return result;
        } else {
            Project project = (Project) result;
            sql =
                    "SELECT * FROM issue WHERE id_project = " + project.getId();

            try {
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                while (resultSet.next()) {
                    Issue issue = new Issue();
                    issue = issue.resultSetToObject(resultSet);
                    project.addIssue(issue);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            sql =
                    "SELECT * FROM project_user WHERE id_project = " + project.getId();

            try {
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                while (resultSet.next()) {
                    int userId = resultSet.getInt(3);
                    User user = new User();
                    user.setId(userId);
                    user = (User) db.select(user);
                    project.addUser(user);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return project;
        }
    }

    /**
     * Aktualizuje odpowiednik obiektu w tabeli
     *
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}
     * @return Liczba zaktualizowanych wierszy
     */
    @SuppressWarnings("SqlResolve")
    public int update(Object object) {

        //sprawdzanie typu klasy
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }

        //wysyłanie zapytania
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeUpdateSql();
        int changes = 0;
        try {
            changes = connection.createStatement().executeUpdate(sql);
            LOG.info("Zaktualizowane pozycje: " + changes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (c == Project.class) {

            Project project = (Project) object;

            ArrayList<Issue> issues = project.getIssues();
            ArrayList<User> users = project.getUsers();
            ResultSet results;

            //sprawdź czy issue istnieje
            //jeśli nie, stwórz issue i stwórz project_issue
            //jeśli tak, tylko update issue

            for (Issue issue : issues
                    ) {
                sql =
                        "SELECT * FROM issue WHERE id = " + issue.getId();

                try {
                    results = db.connection.createStatement().executeQuery(sql);
                    results.last();
                    if (results.getRow() > 0) {
                        changes = changes + db.update(issue);

                    } else {
                        db.insert(issue);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            for (User user : users
                    ) {
                sql =
                        "SELECT * FROM user WHERE id = " + user.getId();

                try {
                    results = db.connection.createStatement().executeQuery(sql);
                    results.last();
                    if (results.getRow() > 0) {
                        changes = changes + db.update(user);

                    } else {
                        user = (User) db.insert(user);
                        sql =
                                "INSERT INTO project_user(id_project, id_user) " +
                                        "VALUES (" + project.getId() + "," + user.getId() + ")";
                        changes = changes + db.connection.createStatement().executeUpdate(sql);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return changes;
    }

    public ArrayList<User> getUsersList() {

        ArrayList<User> list = new ArrayList<>();
        ResultSet results = null;
        User user = new User();

        //pobieramy wszystkich do jednego resultseta
        String sql = "SELECT * FROM user";

        try {
            results = connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            //Iterujemy po resultsecie, resultset zaczyna się przed pierwszym wierszem,
            // a next daje następny czyli 1,2,3,...

            while (results.next()) {
                //zamieniamy na obiekt i dodajemy do listy
                user = user.resultSetToObject(results);
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ArrayList<Project> getProjectList() {

        ArrayList<Project> list = new ArrayList<>();
        ResultSet results = null;
        Project project = new Project();

        String sql = "SELECT id,title,description FROM project";

        try {
            results = connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            while (results.next()) {
                project = project.resultSetToObject(results);
                list.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public DatabaseServerConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(DatabaseServerConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
