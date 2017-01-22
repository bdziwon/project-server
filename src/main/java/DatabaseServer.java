import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class DatabaseServer {

    private static DatabaseServer db = null;
    private Statement statement;
    private DatabaseServerConnectionInfo connectionInfo = null;

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


    public static void main(String[] args) {
        DatabaseServer db = DatabaseServer.getInstance();
        db.createTablesIfDoesNotExists();
    }


    public Statement connect(DatabaseServerConnectionInfo connectionInfo) throws SQLException {
        Connection connection;
        String link = "";
        String username = connectionInfo.getUsername();
        String password = connectionInfo.getPassword();

        try {
            link = connectionInfo.makeDbLink();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (username == null || password == null) {
            connection = DriverManager.getConnection(link);
        } else {
            connection = DriverManager.getConnection(link, username, password);
        }
        if (connection == null) {
            return null;
        }
        this.statement = connection.createStatement();
        this.connectionInfo = connectionInfo;
        return statement;
    }

    public void createDatabaseIfDoesNotExists(String database) {
        String sql =
                "CREATE DATABASE `"+database+"` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci";
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            if (e.toString().contains("database exists")) {
                System.out.println("Tworzenie bazy: Baza już istnieje");
            } else {
                e.printStackTrace();
            }
        }
    }

    public void chooseDatabase(String database) {
        try {
            Connection c = statement.getConnection();
            c.setCatalog(database);
            this.statement = c.createStatement();
        } catch (SQLException e) {
            System.out.println("Zmiana bazy nie powiodła się");
        }
    }

    public void createTablesIfDoesNotExists() {
        try {
            String sql =
                    "CREATE TABLE IF NOT EXISTS user (" +
                            "id       INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "name     VARCHAR(50)                         NOT NULL DEFAULT 'pusto'," +
                            "surname  VARCHAR(50)                         NOT NULL DEFAULT 'pusto'," +
                            "jobTitle ENUM('PROGRAMISTA','TESTER','ADMINISTRATOR') NOT NULL)";
                            //W takim wierszu domyślnym jest pierwsza wartość enuma

            statement.executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS issue (" +
                            "id          INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "title       VARCHAR(50)                         NOT NULL DEFAULT 'Brak tytułu'," +
                            "description VARCHAR(150)                        NOT NULL DEFAULT 'Brak opisu'," +
                            "priority    ENUM('ZWYKŁY','NORMALNY', 'WYSOKI') NOT NULL)";
                            //W takim wierszu domyślnym jest pierwsza wartość enuma

            statement.executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS project (" +
                            "id          INT(5)                           NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "title       VARCHAR(50)                      NOT NULL DEFAULT 'Brak tytułu'," +
                            "description VARCHAR(150)                     NOT NULL DEFAULT 'Brak opisu')";
                            //bez pól do błędów i użytkowników, potrzebne osobne tabele project_user, project_issue

            statement.executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS project_issue(" +
                            "id         INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project INT(5)                              NOT NULL," +
                            "id_issue   INT(5)                              NOT NULL," +
                            "CONSTRAINT project_fk FOREIGN KEY (id_project) REFERENCES project(id)" +
                            "ON DELETE CASCADE ," +
                            "CONSTRAINT issue_fk   FOREIGN KEY (id_issue)   REFERENCES issue(id)" +
                            "ON DELETE CASCADE )";

            statement.executeUpdate(sql);

            sql =
                    "CREATE TABLE IF NOT EXISTS project_user(" +
                            "id         INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project INT(5)                              NOT NULL," +
                            "id_user    INT(5)                              NOT NULL," +
                            "CONSTRAINT project_fk_2 FOREIGN KEY (id_project) REFERENCES project(id)" +
                            "ON DELETE CASCADE," +
                            "CONSTRAINT user_fk      FOREIGN KEY (id_user)    REFERENCES user(id)" +
                            "ON DELETE CASCADE)";

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wstawia obiekt do odpowiedniej tabeli
     * UWAGA: INSERT nie wrzuca błędów i użytkowników, to robi UPDATE.
     * Insert wrzuca projekt z ustawionymi własnymi parametrami
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}
     * @return Obiekt z wypełnionym polem ID odpowiadającym polu w bazie
     */
    public Object insert(Object object) throws IllegalArgumentException {

        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }
        //Dla każdego obiektu Issue, Project, user zawsze stworzy poprawnego inserta
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface)object;
        String sql = sqlInterface.makeInsertSql();
        System.out.println(sql);
        try {
            int changes = statement.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
            if (changes == 0) {
                System.out.println("Błąd insert, brak zmian w tabeli");
            } else {
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
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}  <br>
     *               ID obiektu musi być większe od -1
     * @return int z liczbą usuniętych wierszy
     * @throws IllegalArgumentException
     */
    public int delete(Object object) throws IllegalArgumentException {
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeDeleteSql();
        System.out.println(sql);
        try {
            int changes = statement.executeUpdate(sql);
            System.out.println("Usuniętych pozycji: "+changes);
            return changes;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Aktualizuje odpowiednik obiektu w tabeli
     * @param object Obiekt klasy {@link Issue} {@link User} lub {@link Project}
     * @return Liczba zaktualizowanych wierszy
     */
    public int update(Object object) {
        Class<?> c = object.getClass();
        if (Issue.class != c && Project.class != c && User.class != c) {
            throw new IllegalArgumentException("Obiekt klasy Issue, Project lub user EXPECTED");
        }
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface) object;
        String sql = sqlInterface.makeUpdateSql();
        System.out.println(sql);
        int changes = 0;
        try {
            changes = statement.executeUpdate(sql);
            System.out.println("Zaktualizowane pozycje: "+changes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (c == Project.class) {

            //TODO: Dla Project musi dodatkowo dodawać wiersze do tabel łączących
            Project project = (Project) object;

            ArrayList<Issue> issues = project.getIssues();
            ArrayList<User>  users = project.getUsers();
            ResultSet results;

            //sprawdź czy issue istnieje
            //jeśli nie, stwórz issue i stwórz project_issue
            //jeśli tak, tylko update issue

            for (Issue issue : issues
                 ) {
                sql =
                        "SELECT * FROM issue WHERE id = "+issue.getId();

                try {
                    results = db.statement.executeQuery(sql);
                    results.last();
                    if (results.getRow() > 0) {
                        System.out.println("Issue istnieje");
                        changes = changes + db.update(issue);

                    } else {
                        System.out.println("Issue nie istnieje");
                        issue = (Issue) db.insert(issue);
                        sql =
                                "INSERT INTO project_issue(id_project, id_issue) " +
                                        "VALUES ("+project.getId()+","+issue.getId()+")";
                        changes = changes + db.statement.executeUpdate(sql);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            for (User user : users
                    ) {
                sql =
                        "SELECT * FROM user WHERE id = "+user.getId();

                try {
                    results = db.statement.executeQuery(sql);
                    results.last();
                    if (results.getRow() > 0) {
                        changes = changes + db.update(user);

                    } else {
                        user = (User) db.insert(user);
                        sql =
                                "INSERT INTO project_user(id_project, id_user) " +
                                        "VALUES ("+project.getId()+","+user.getId()+")";
                        changes = changes + db.statement.executeUpdate(sql);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }            
            
        }
        return changes;
    }


}
