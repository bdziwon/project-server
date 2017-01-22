/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class Issue implements Cloneable {
	
	
    private int id;
    private String title;
    private String description;
    private String priority;    
    
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
    
}
