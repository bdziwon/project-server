import org.junit.Test;
import java.sql.*;
import java.sql.Connection;
import static org.assertj.core.api.Assertions.assertThat;

public class DatabaseServerTest {

    @Test
    public void getInstanceShouldReturnSameObject() {
        DatabaseServer obj = DatabaseServer.getInstance();
        DatabaseServer obj2 = DatabaseServer.getInstance();
        assertThat(obj).isEqualTo(obj2);
    }

    @Test
    public void databaseBasicOperationsTest() {

        //connect
        DatabaseServerConnectionInfo connectionInfo =
                new DatabaseServerConnectionInfo("localhost", "3306", "database");
        connectionInfo.setUsername("root");
        connectionInfo.setPassword("");
        connectionInfo.setDatabase("database");

        DatabaseServer db = DatabaseServer.getInstance();
        try {
            Connection connection = db.connect(connectionInfo);
            assertThat(connection.createStatement()).isNotNull();
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
        Project project = new Project();
        project.setTitle("Mój projekt");
        Issue issue = new Issue();
        Issue issue2 = new Issue();
        issue.setTitle("Błąd 1");
        issue2.setTitle("Błąd 2");
        project.addIssue(issue);
        project.addIssue(issue2);

        User user = new User();
        User user2 = new User();
        user.setName("Heniek");
        user2.setName("Maniek");
        project.addUser(user);
        project.addUser(user2);

        project = (Project) db.insert(project);

        //update
        //!! ISSUE musi mieć ustawione id projektu przed dodaniem lub wywołaniem bo inaczej będzie wyjątek sqla
        issue.setProjectId(project.getId());
        issue2.setProjectId(project.getId());
        {
            int changes = db.update(project);
            assertThat(changes).isEqualTo(3);
        }
        int userid = user.getId();

        issue.setTitle("Błąd 3");
        user.setName("Bożena");
        {
            int changes = db.update(project);
            assertThat(changes).isEqualTo(5);
        }
        //select
        {
            //project
            Project selectedProject = new Project();
            selectedProject.setId(project.getId());
            selectedProject = (Project) db.select(selectedProject);
            assertThat(selectedProject.getIssues().size()).isEqualTo(2);
            assertThat(selectedProject.getUsers().size()).isEqualTo(2);

            //user
            user = new User();
            System.out.println(user.getName());
            user.setId(userid);
            User newUser = (User) db.select(user);
            assertThat(newUser.getName()).isNotEqualTo(user.getName());
        }
        //delete
        {
            int changes = db.delete(project);
            assertThat(changes).isEqualTo(1);
        }

        //Po teście powinno zostać jedynie dwóch nowych użytkowników, Project i Issue powinien być usunięty.


    }
}
