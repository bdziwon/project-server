import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.*;

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
                            "CONSTRAINT project_fk FOREIGN KEY (id_project) REFERENCES project(id)," +
                            "CONSTRAINT issue_fk   FOREIGN KEY (id_issue)   REFERENCES issue(id))";

            statement.executeUpdate(sql);


            sql =
                    "CREATE TABLE IF NOT EXISTS project_user(" +
                            "id         INT(5)                              NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                            "id_project INT(5)                              NOT NULL," +
                            "id_user    INT(5)                              NOT NULL," +
                            "CONSTRAINT project_fk_2 FOREIGN KEY (id_project) REFERENCES project(id)," +
                            "CONSTRAINT user_fk      FOREIGN KEY (id_user)    REFERENCES user(id))";

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(Object object) {

        //Dla każdego obiektu Issue, Project, user zawsze stworzy poprawnego inserta
        DatabaseSqlInterface sqlInterface = (DatabaseSqlInterface)object;
        String sql = sqlInterface.makeInsertSql();
        System.out.println(sql);
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Class<?> c = object.getClass();
            if (c == Project.class) {
                //TODO: Dla Project musi dodatkowo dodawać wiersze do tabel łączących
            }
    }

    public void delete(Object object) {
        //todo: delete na wzór insert
    }

    public void update(Object object) {
        //todo: update na wzór insert
    }


}
