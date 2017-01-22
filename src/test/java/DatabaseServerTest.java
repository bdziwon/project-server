import org.junit.Test;
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
        connectionInfo.setDatabase("database");

        DatabaseServer db = DatabaseServer.getInstance();
        try {
            Statement statement;
            statement = db.connect(connectionInfo);
            assertThat(statement).isNotNull();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //create database
        db.createDatabaseIfDoesNotExists(   "database");

        //choose database
        db.chooseDatabase("database");

        //create tables
        db.createTablesIfDoesNotExists();

        //insert

        Project project = new Project();
        project.setTitle("Mój projekt");
        Issue issue  = new Issue();
        Issue issue2 = new Issue();
        issue.setTitle("Błąd 1");
        issue2.setTitle("Błąd 2");
        project.addIssue(issue);
        project.addIssue(issue2);

        User user  = new User();
        User user2 = new User();
        user.setName("Heniek");
        user2.setName("Maniek");
        project.addUser(user);
        project.addUser(user2);

        project = (Project) db.insert(project);
        {
            int changes = db.update(project);
            assertThat(changes).isEqualTo(5);
        }
        issue.setTitle("Błąd 3");
        user.setName("Bożena");
        {
            int changes = db.update(project);
            assertThat(changes).isEqualTo(5);
        }
        {
            int changes = db.delete(project);
            assertThat(changes).isEqualTo(1);
        }
    }
}
