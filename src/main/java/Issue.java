/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class Issue implements Cloneable, DatabaseSqlInterface {
	
	
    private int id  = -1;
    private int projectId = -1;
    private String title        = "Brak tytułu";
    private String description  = "Brak opisu";
    private String priority     = "ZWYKŁY";
    
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
    
    public String getPriority(){
    return this.priority;
    }    
       
    public void setPriority(String priority){
    this.priority=priority;
    }

    public int getId() {
        return id;
    }

    @Override
    public String makeUpdateSql() {
        String sql =
                "UPDATE issue " +
                        "SET " +
                        "title = '"+getTitle()+"', " +
                        "description = '"+getDescription()+"', " +
                        "priority = '"+getPriority()+"' "+
                        "WHERE id = "+getId();
        return sql;
    }

    @Override
    public String makeDeleteSql() {
        String sql =
                "DELETE FROM issue WHERE id="+getId();
        return sql;
    }

    @Override
    public String makeInsertSql() {
        String sql =
                "INSERT INTO issue(id_project,title,description,priority) " +
                        "VALUES ('"+getProjectId()+"','"+getTitle()+"','"+getDescription()+"','"+getPriority()+"')";

        return sql;
    }

    @Override
    public String makeSelectSql() {
        String sql =
                "SELECT * FROM issue WHERE id = "+getId();
        return sql;
    }

    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
