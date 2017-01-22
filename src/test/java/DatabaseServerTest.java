import com.mysql.cj.api.jdbc.JdbcConnection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class DatabaseServerTest {

    @Test
    public void getInstanceShouldReturnSameObject() {
        DatabaseServer obj  = DatabaseServer.getInstance();
        DatabaseServer obj2 = DatabaseServer.getInstance();
        assertThat(obj).isEqualTo(obj2);
    }

    @Test
    public void databaseBasicOperationsTest() {

        //connect
        DatabaseServerConnectionInfo connectionInfo =
                new DatabaseServerConnectionInfo("localhost","3306", "database");
        connectionInfo.setUsername("root");
        connectionInfo.setPassword("");

        DatabaseServer db = DatabaseServer.getInstance();
        try {
            Statement statement;
            statement = db.connect(connectionInfo);
            assertThat(statement).isNotNull();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //create database
        db.createDatabaseIfDoesNotExists("database");

        //choose database
        db.chooseDatabase("database");

        //create tables
        db.createTablesIfDoesNotExists();

        //TODO: insert
        User user = new User();
        user.setName("Bartek");
        user.setSurname("Dz");
        user.setJobTitle("PROGRAMISTA");

        int id = user.getId();
        user = (User) db.insert(user);
        int newId = user.getId();
        assertThat(id).isNotEqualTo(newId);
        assertThat(newId).isGreaterThan(0);

        //TODO: update

        //TODO: delete
        int changes = db.delete(user);
        assertThat(changes).isGreaterThan(0);

    }
}
