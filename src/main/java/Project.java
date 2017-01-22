import java.util.ArrayList;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class Project implements DatabaseSqlInterface {

	private int id = -1;
    private String title            = "Brak tytułu";
    private String description      = "Brak opisu";
    private ArrayList<Issue> issues = new ArrayList<Issue>();
    private ArrayList<User> users   = new ArrayList<User>();
    
    public String getTitle(){
    return this.title;
    }

    public void setTitle(String title){
    this.title=title;
    }
    
    public String getDescription(){
    return this.description;
    }    
       
    public void setDescription(String description){
    this.description=description;
    }

    public int getId() {
        return id;
    }

    @Override
    public String makeUpdateSql() {
        //todo: update
        return null;
    }

    @Override
    public String makeDeleteSql() {
        String sql =
                "DELETE FROM project WHERE id="+getId();
        return sql;
    }

    public String makeInsertSql() {
        String sql =
                "INSERT INTO project(title, description) " +
                        "VALUES ('"+getTitle()+"','"+getDescription()+"')";

        return sql;
    }

    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }
}
