
package logic;
import java.io.*;

public class Task implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String description;
    private boolean isCompleted;
    private ListTask list;
    
    public String getDescription(){
        return this.description;
    }

    public boolean getIsCompleted() {
        return this.isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
   
    public void setDescription(String newDescription){
        this.description = newDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public Task(){
        
    }
    
    public Task(String description){
        setDescription(description);
        isCompleted = false;
    }

    public Task(int id, String description, boolean isCompleted, ListTask list) {
        this.id = id;
        this.description = description;
        this.isCompleted = isCompleted;
        this.list = list;
    }
    
    public Task(String description, boolean isCompleted, ListTask list) {
        this.description = description;
        this.isCompleted = isCompleted;
        this.list = list;
    }
    
    
}
