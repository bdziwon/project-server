import java.util.ArrayList;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class Project {

	private int id;
    private String title;
    private String description;
    private ArrayList<Issue> issues;
    private ArrayList<User> users;
    
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
}
