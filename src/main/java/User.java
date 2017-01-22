import java.util.ArrayList;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */

public class User {
	
	private int id;
    private String name;
    private String surname;
    private String jobTitle;   

    public int getId(){
    return this.id;
    }

    public void setId(int id){
    this.id=id;
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
    
    public String getJobtitle(){
    return this.jobTitle;
    }

    public void setJobtitle(String jobTitle){
    this.jobTitle=jobTitle;
    } 
}
