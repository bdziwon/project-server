import java.util.StringJoiner;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */

public class User implements DatabaseSqlInterface {
	
	private int id = -1;
    private String name     = "pusto";
    private String surname  = "pusto";
    private String jobTitle = "PROGRAMISTA";

    public int getId(){
    return this.id;
    }

    public String getName(){
    return this.name;
    }

    public void setName(String name){
    this.name=name;
    }
    
    public String getSurname(){
    return this.surname;
    }

    public void setSurname(String surname){
    this.surname=surname;
    }
    
    public String getJobTitle(){
    return this.jobTitle;
    }

    public void setJobTitle(String jobTitle){
    this.jobTitle=jobTitle;
    }

    @Override
    public String makeUpdateSql() {
        //todo: update
        return null;
    }

    @Override
    public String makeDeleteSql() {
        //todo: delete
        return null;
    }

    @Override
    public String makeInsertSql() {
        String sql =
                "INSERT INTO user(name,surname,jobTitle) " +
                        "VALUES ('"+getName()+"','"+getSurname()+"','"+getJobTitle()+"')";

        return sql;
    }

    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }
}
