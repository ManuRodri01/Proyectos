
package logic;
import java.io.*;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Task implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    @Basic
    private String description;
    private boolean isCompleted;
    
    @ManyToOne
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
    
    public void deleteThisFromDB(LogicController logicController){
        logicController.deleteTask(this.id);
    }
    
    public Task(){
        
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
