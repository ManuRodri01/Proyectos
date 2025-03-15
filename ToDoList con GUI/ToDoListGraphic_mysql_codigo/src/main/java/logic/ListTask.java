 
package logic;

import java.io.*;
import java.util.ArrayList;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ListTask implements Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    private int id;
    @Basic
    private String listName;
    @OneToMany(mappedBy="list")
    private ArrayList<Task> list;
    
    @ManyToOne
    private User user;
    
    public String getListName(){
        return this.listName;
    }
    
    public void setListName(String newName){
        this.listName = newName;
    }

    public ArrayList<Task> getList() {
        return list;
    }

    public void setList(ArrayList<Task> list) {
        this.list = list;
    }
    
    public boolean hasTasks(){
        return !this.list.isEmpty();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
    public void deleteThisFromDB(LogicController logicController){
        for(Task task : list){
            task.deleteThisFromDB(logicController);
        }
        logicController.deleteList(this.id);
    }
    
    public ListTask() {
    }

    public ListTask(int id, String listName, ArrayList<Task> list, User user) {
        this.id = id;
        this.listName = listName;
        this.list = list;
        this.user = user;
    }
    
    public ListTask(String listName, ArrayList<Task> list, User user) {
        this.listName = listName;
        this.list = list;
        this.user = user;
    }
    
}
