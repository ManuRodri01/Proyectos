 
package logic;

import java.io.*;
import java.util.ArrayList;


public class ListTask implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String listName;
    private ArrayList<Task> list;
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
