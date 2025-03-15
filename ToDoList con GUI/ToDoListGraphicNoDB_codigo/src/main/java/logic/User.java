
package logic;
import java.io.*;
import java.util.ArrayList;


public class User implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String password;
    private String urlImage = "";
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

    public String getImage() {
        return urlImage;
    }

    public void setImage(String imagen) {
        this.urlImage = imagen;
    }
    
    public boolean existImage(){
        File image = new File(urlImage);
        return urlImage.length() != 0 && image.exists();
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
    
    
    public User(){
        
    }

    public User(String username, String password, ArrayList<ListTask> listOfLists) {
        this.username = username;
        this.password = password;
        this.listOfLists  = listOfLists;
        urlImage = "";
    }    
    
}
