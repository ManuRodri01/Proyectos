
package logic;

import java.util.ArrayList;
import persistence.Keeper;

public class LogicController {

    Keeper keeper = new Keeper();
    //--------------------User--------------------
    public boolean createUser(User user){
        return keeper.createUser(user);
    }
    
    public boolean createUser(String username, String password){
        ArrayList<ListTask> list = new ArrayList<>();
        return createUser(new User(username, password,list));
    }
    
    public void deleteUser(String username){
        keeper.deleteUser(username);
    }
    
    public User findUser(String username){
        return keeper.getUserInfo(username);
    }
    
    public void editUser(User user){
        keeper.saveUser(user);
    }
    
}
