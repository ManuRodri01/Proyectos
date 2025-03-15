
package logic;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import persistence.PersistenceController;

public class LogicController {
    PersistenceController controlPersis = new PersistenceController();
    
    //--------------------User--------------------
    public boolean createUser(User user){
        return controlPersis.createUser(user);
    }
    
    public boolean createUser(String username, String password){
        ArrayList<ListTask> list = new ArrayList<>();
        return createUser(new User(username, password,list));
    }
    
    public void deleteUser(String username){
        controlPersis.deleteUser(username);
    }
    
    public User findUser(String username){
        return controlPersis.findUser(username);
    }
    
    public ArrayList<User> findAllUsers(){
        return controlPersis.findAllUsers();
    }
    
    public void editUser(User user){
        controlPersis.editUser(user);
    }
    
    public byte[] convertImageToByte(Image image){
        BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "PNG", stream);
        } catch (IOException ex) {
            System.out.println("Error al serializar imagen");
        }
        
        return stream.toByteArray();
    }
    
    public void addImageToUser(User user, Image image){
        user.setImage(convertImageToByte(image));
    }
    
    
    
    //--------------------List--------------------
    public void createList(ListTask list){
        controlPersis.createList(list);
    }
    
    public void deleteList(int id){
        controlPersis.deleteList(id);
    }
    
    public ListTask findList(int id){
        return controlPersis.findList(id);
    }
    
    public ArrayList<ListTask> findAllLists(){
        return controlPersis.findAllLists();
    }
    
    public void editList(ListTask list){
        controlPersis.editList(list);
    }
    
    
    //--------------------Task--------------------
    public void createTask(Task task){
        controlPersis.createTask(task);
    }
    
    public void deleteTask(int id){
        controlPersis.deleteTask(id);
    }
    
    public Task findTask(int id){
        return controlPersis.findTask(id);
    }
    
    public ArrayList<Task> findAllTasks(){
        return controlPersis.findAllTasks();
    }
    
    public void editTask(Task task){
        controlPersis.editTask(task);
    }
}
