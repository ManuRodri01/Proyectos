
package persistence;

import java.io.*;
import java.util.HashMap;
import logic.*;

public class Keeper {
    private HashMap<String, UserPassword> mapUsers;
    private String save_path = System.getProperty("user.dir") + File.separator + "Guardados";

    public Keeper() {
        File saves = new File(save_path);
        if(!saves.exists()){
            saves.mkdir();
        }
        
        
        try{
            ObjectInputStream usersInput = new ObjectInputStream(new FileInputStream(save_path + File.separator + "usersList.dat"));
            try {
                mapUsers = (HashMap<String, UserPassword>) usersInput.readObject();
            }
            catch (ClassNotFoundException ex) {
                
                }
            usersInput.close();
        }
        catch(FileNotFoundException ex){
                mapUsers = new HashMap<>();
                saveListUsers();
            }
        catch (IOException ex) {
                
            }
            
        
    }
    
    public void saveUser(User user){
        UserPassword userPassword =  mapUsers.get(user.getUsername());
        if(userPassword == null){
            userPassword = new UserPassword((user.getUsername() + ".dat"));
            mapUsers.put(user.getUsername(), userPassword);
            
        }
        try {
            ObjectOutputStream userToSave = new ObjectOutputStream(new FileOutputStream(save_path + File.separator + userPassword.getFile()));
            userToSave.writeObject(user);
            userToSave.close();
        }
        catch(IOException ex) {
                
            }
        
        
    }
    
    public boolean createUser(User user){
        UserPassword userPassword =  mapUsers.get(user.getUsername());
        if(userPassword == null){
            userPassword = new UserPassword((user.getUsername() + ".dat"));
            mapUsers.put(user.getUsername(), userPassword);
            try {
                ObjectOutputStream userToSave = new ObjectOutputStream(new FileOutputStream(save_path + File.separator + userPassword.getFile()));
                userToSave.writeObject(user);
                userToSave.close();
            }
            catch(IOException ex) {

                }
            return true;
        }
        
        return false;
    }
    
    public User getUserInfo(String username){
        User user;
        if(!mapUsers.containsKey(username)){
            System.out.println("Usuario no existente");
            return null;
        }
        else{
            UserPassword userinfo = mapUsers.get(username);
                try{
                    ObjectInputStream input = new ObjectInputStream(new FileInputStream(save_path + File.separator + userinfo.getFile()));
                    user = (User) input.readObject();
                    input.close();
                    return user;
                    }
                catch(FileNotFoundException ex){
                   return null;
                    
                }
                catch(IOException e){
                    return null;
                    
                } catch (ClassNotFoundException ex) {
                    return null;
                }
                
            
        }
        
    }
    
    public boolean userExists(String username){
        return mapUsers.containsKey(username);
    }
    
    public void deleteUser(String username){
        if(mapUsers.containsKey(username)){
            UserPassword userinfo = mapUsers.get(username);
            File user_path = new File(save_path + File.separator + userinfo.getFile());
            user_path.delete();
            mapUsers.remove(username);
            saveListUsers();
            System.out.println("Usuario Eliminado exitosamente");
            
            
        }
        
    }
    
    public void saveListUsers(){
        try {
            ObjectOutputStream list_of_users = new ObjectOutputStream(new FileOutputStream(save_path + File.separator + "usersList.dat"));
            list_of_users.writeObject(mapUsers);
            list_of_users.close();
        }
        catch (IOException ex) {
            
            }
    }
    
}

class UserPassword implements Serializable{
    String file;

    public UserPassword() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

   

    
    
    public UserPassword(String file) {
        this.file = file;
    }
    
    
}
