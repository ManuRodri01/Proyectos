
package logic;

import java.io.*;
import java.util.ArrayList;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

@Entity
public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    
    @Id
    private String username;
    @Basic
    private String password;
    @Lob
    private byte[] image;
    
    @OneToMany(mappedBy="user")
    private ArrayList<ListTask> listOfLists;
    
    public void addNewList(ListTask newList){
        this.listOfLists.add(newList);
        
    }
       
    public void removeList(ListTask list){
        this.listOfLists.remove(list);
    }
    
    public boolean hasLists(){
        return !listOfLists.isEmpty();
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] imagen) {
        this.image = imagen;
    }
    
    public boolean existImage(){
        return image.length != 0;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<ListTask> getListOfLists() {
        return listOfLists;
    }

    public void setListOfLists(ArrayList<ListTask> listOfLists) {
        this.listOfLists = listOfLists;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void deleteThisFromDB(LogicController logicController){
        for(ListTask list : listOfLists){
            list.deleteThisFromDB(logicController);
        }
        logicController.deleteUser(this.username);
    }
    
    public User(){
        
    }

    public User(String username, String password, ArrayList<ListTask> listOfLists) {
        this.username = username;
        this.password = password;
        this.listOfLists  = listOfLists;
        image = new byte[0];
    }    
    
}
