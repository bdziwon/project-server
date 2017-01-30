package sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import util.*;
import util.interfaces.DatabaseSqlInterface;

import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Singleton do obsĹ‚ugi bazy danych
 * ObsĹ‚uga:
 * WywoĹ‚ujemy getInstance(), nastÄ™pnie pierwszy raz musimy siÄ™ poĹ‚Ä…czyÄ‡ za pomocÄ… connect
 * oraz stworzyÄ‡ bazy za pomocÄ… createDatabaseIfDoesNotExists i createTablesIfDoesNotExists.
 * potem moĹĽemy wywoĹ‚ywaÄ‡ resztÄ™ metod
 */

public class DatabaseServer {


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
     * Ĺ‚Ä…czenie z bazÄ…
     *
     * @param connectionInfo musi zawieraÄ‡ poprawnÄ…
     *                       nazwÄ™ uĹĽytkownika, hasĹ‚o, nazwÄ™ hosta, i port
     * @return instancja poĹ‚Ä…czenia {@link Connection}
     * @throws SQLException SQLException przy zĹ‚ych hasĹ‚ach itd
     */
    public Connection connect(DatabaseServerConnectionInfo connectionInfo) throws IllegalArgumentException, SQLException {
        if (this.connection != null) {
            LOG.info("PoĹ‚Ä…czenie nie udane, juĹĽ poĹ‚Ä…czono");
            return this.connection;
        }

        Connection connection;
        String link;
        String username = connectionInfo.getUsername();
        String password = connectionInfo.getPassword();

        //sprawdzanie czy link zostaĹ‚ poprawnie stworzony
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

            //Ĺ‚Ä…czenie z mysql
            if (username == null) {
                LOG.info("username jest nullem, prĂłba Ĺ‚Ä…czenia bez loginu i hasĹ‚a");
                connection = DriverManager.getConnection(link);
            } else {
                LOG.info("Ĺ‚Ä…czenie za pomocÄ… hasĹ‚a");
                connection = DriverManager.getConnection(link, username, password);
            }
        } catch (SQLException e) {
            throw e;
        }

        //nie chcemy nulla
        if (connection == null) {
            LOG.error("PoĹ‚Ä…czenie nieudane, zwrĂłcono null");
            return connection;
        }

        LOG.info("PoĹ‚aczenie udane!");
        this.connectionInfo = connectionInfo;
        this.connection = connection;
        return connection;
    }

    /**
     * Tworzy bazÄ™ danych "database" i wybiera jÄ… w {@link Connection}
     */
    public void createDatabaseIfDoesNotExists() {
        String sql =
                "CREATE DATABASE `" + "database" + "` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";

        try {
            connection.createStatement().executeUpdate(sql);
            connection.setCatalog("database");
        } catch (SQLException e) {
            if (e.toString().contains("database exists")) {
                //ObsĹ‚ugiwany wyjÄ…tek

                LOG.info("Baza juĹĽ istnieje");
                try {
                    connection.setCatalog("database");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }

            } else {
                //Nieoczekiwany wyjÄ…tek

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
                            "title       VARCHAR(50)                      NOT NULL DEFAULT 'Brak tytuĹ‚u'," +
                            "description VARCHAR(150)                     NOT NULL DEFAULT 'Brak opisu')";
            //bez pĂłl do bĹ‚Ä™dĂłw i uĹĽytkownikĂłw, potrzebne osobne tabele project_user, project_issue

            connection.createStatement().executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS issue (" +
                            "id          INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project  INT(5)                              NOT NULL," +
                            "title       VARCHAR(50)                         NOT NULL DEFAULT 'Brak tytuĹ‚u'," +
                            "description VARCHAR(150)                        NOT NULL DEFAULT 'Brak opisu'," +
                            "priority    ENUM('ZWYKĹ�Y','NORMALNY', 'WYSOKI') NOT NULL, " +
                            "CONSTRAINT project_fk FOREIGN KEY (id_project) REFERENCES project(id) " +
                            "ON DELETE CASCADE)";
            //W takim wierszu domyĹ›lnym jest pierwsza wartoĹ›Ä‡ enuma

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
     * UWAGA: INSERT nie wrzuca bĹ‚Ä™dĂłw i uĹĽytkownikĂłw projektĂłw, to robi UPDATE.
     * Aby wrzuciÄ‡ caĹ‚y projekt robimy najpierw insert, pĂłĹşniej update
     * Insert wrzuca projekt z ustawionymi wĹ‚asnymi parametrami
     *
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}
     * @return Obiekt z wypeĹ‚nionym polem ID odpowiadajÄ…cym polu w bazie
     */
    public Object insert(Object object) throws IllegalArgumentException {

        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }

        //Dla kaĹĽdego obiektu Issue, Project, user zawsze stworzy poprawnego inserta
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
     * Usuwa obiekt z tabeli sprawdzajÄ…c id
     *
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}  <br>
     *               ID obiektu musi byÄ‡ wiÄ™ksze od -1
     * @return int z liczbÄ… usuniÄ™tych wierszy
     * @throws IllegalArgumentException WyjÄ…tek gdy damy obiekt niewspieranej klasy
     */
    public int delete(Object object) throws IllegalArgumentException {

        //sprawdzanie typu klasy
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }
        //wysyĹ‚anie zapytania
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeDeleteSql();
        try {
            int changes = connection.createStatement().executeUpdate(sql);
            LOG.info("UsuniÄ™tych pozycji: " + changes);
            return changes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Pobiera Usera z bazy na podstawie loginu i hasĹ‚a
     * @param credentials obiekt zawierajÄ…cy login i hasĹ‚o
     * @return zwraca Usera o loginie i haĹ›le lub null jeĹ›li nie znaleziono
     */
    public User select(Credentials credentials) {

        ResultSet   resultSet   = null;
        String      login       = credentials.getLogin();
        String      password    = credentials.getPassword();
        User        user        = new User();

        String sql =
                "SELECT * FROM user " +
                        "WHERE login = '"+login+"' AND " +
                        "password = '"+password+"'";

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

    @SuppressWarnings("SqlResolve")
    public Object select(Object object) throws IllegalArgumentException {

        //sprawdzanie typu klasy
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }

        //wysyĹ‚anie zapytania
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

        //wysyĹ‚anie zapytania
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

            //sprawdĹş czy issue istnieje
            //jeĹ›li nie, stwĂłrz issue i stwĂłrz project_issue
            //jeĹ›li tak, tylko update issue

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

    public ArrayList<User> getUserList() {

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
            //Iterujemy po resultsecie, resultset zaczyna siÄ™ przed pierwszym wierszem,
            // a next daje nastÄ™pny czyli 1,2,3,...

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
