/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class Issue implements Cloneable, DatabaseSqlInterface {
	
	
    private int id;
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
        //TODO: update
        return null;
    }

    @Override
    public String makeDeleteSql() {
        //TODO: delete
        return null;
    }

    @Override
    public String makeInsertSql() {
        String sql =
                "INSERT INTO issue(title,description,priority) " +
                        "VALUES ('"+getTitle()+"','"+getDescription()+"','"+getPriority()+"')";

        return sql;
    }
}
