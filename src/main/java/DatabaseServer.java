import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.*;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class DatabaseServer {

    private static DatabaseServer db = null;
    private Statement statement;

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
        return statement;
    }

    public int createTablesIfDoesNotExists() {
        //TODO : Tabele Project, Issue, ProjectUsers, ProjectIssues
        try {
            String sql =
                    "CREATE TABLE IF NOT EXISTS user (" +
                    "id       int(5)      NOT NULL AUTO_INCREMENT PRIMARY KEY,"  +
                    "name     varchar(50) NOT NULL DEFAULT 'empty'," +
                    "surname  varchar(50) NOT NULL DEFAULT 'empty'," +
                    "jobTitle enum('PROGRAMMER','TESTER','ADMIN') NOT NULL DEFAULT 'PROGRAMMER')";

            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void insert(Object object) {
        //todo: insert, rozpoznawanie tabeli to typie obiektu
    }
    public void delete(Object object) {
        //todo: delete, rozpoznawanie tabeli to typie obiektu
    }
    public void update(Object object) {
        //todo: update, rozpoznawanie tabeli to typie obiektu
    }


}
