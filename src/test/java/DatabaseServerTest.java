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

        //insert
        User user = new User();
        Project project = new Project();
        Issue issue = new Issue();
        {
            user.setName("Bartek");
            user.setSurname("Dz");
            user.setJobTitle("PROGRAMISTA");
            int id = user.getId();
            user = (User) db.insert(user);
            int newId = user.getId();
            assertThat(id).isNotEqualTo(newId);
            assertThat(newId).isGreaterThan(0);
        }
        {
            issue.setTitle("title");
            int id = issue.getId();
            issue = (Issue) db.insert(issue);
            int newId = issue.getId();
            assertThat(id).isNotEqualTo(newId);
            assertThat(newId).isGreaterThan(0);
        }
        {
            project.setTitle("title");
            int id = project.getId();
            project = (Project) db.insert(project);
            int newId = project.getId();
            assertThat(id).isNotEqualTo(newId);
            assertThat(newId).isGreaterThan(0);
        }

        //update
        {
            user.setName("Mikołaj");
            int changes = db.update(user);
            assertThat(changes).isGreaterThan(0);
        }
        {
            project.setDescription("New description");
            int changes = db.update(project);
            assertThat(changes).isGreaterThan(0);
        }
        {
            issue.setDescription("New description");
            int changes = db.update(issue);
            assertThat(changes).isGreaterThan(0);
        }
        
        //delete
        {
            int changes = db.delete(user);
            assertThat(changes).isGreaterThan(0);
        }
        {
            int changes = db.delete(project);
            assertThat(changes).isGreaterThan(0);
        }
        {
            int changes = db.delete(issue);
            assertThat(changes).isGreaterThan(0);
        }
    }
}
