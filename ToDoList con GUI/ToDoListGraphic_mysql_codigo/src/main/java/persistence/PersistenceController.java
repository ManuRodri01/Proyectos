
package persistence;

import java.util.ArrayList;
import java.util.List;
import logic.*;
import persistence.exceptions.NonexistentEntityException;
import persistence.exceptions.PreexistingEntityException;

public class PersistenceController {
    UserJpaController userJpa = new UserJpaController();
    ListTaskJpaController listJpa = new ListTaskJpaController();
    TaskJpaController taskJpa = new TaskJpaController();
    
    
    //--------------------User--------------------
    public boolean createUser(User user){
        try{
            userJpa.create(user);
            return true;
        }
        catch (PreexistingEntityException e){
            return false;
        }
        catch(Exception e){
            return false;
        }
        
    }
    
    public void deleteUser(String username){
        try{
            userJpa.destroy(username);
        }
        catch(NonexistentEntityException e){
            
        }
        
    }
    
    public User findUser(String username){
        return userJpa.findUser(username);
    }
    
    public ArrayList<User> findAllUsers(){
        List<User> newList = userJpa.findUserEntities();
        ArrayList<User> listToReturn =  new ArrayList<>(newList); 
        return listToReturn;
    }
    
    public void editUser(User user){
        try {
            userJpa.edit(user);
        }
        catch (Exception ex) {
            
        }
    }
    
    
    //--------------------List--------------------
    public void createList(ListTask list){
        listJpa.create(list);
    }
    
    public void deleteList(int id){
        try {
            listJpa.destroy(id);
        }
        catch (NonexistentEntityException ex) {
            
        }
    }
    
    public ListTask findList(int id){
        return listJpa.findListTask(id);
    }
    
    public ArrayList<ListTask> findAllLists(){
        List<ListTask> newList = listJpa.findListTaskEntities();
        ArrayList<ListTask> listToReturn =  new ArrayList<>(newList); 
        return listToReturn;
    }
    
    public void editList(ListTask list){
        try {
            listJpa.edit(list);
        }
        catch (Exception ex) {
            
        }
    }
    
    
    //--------------------Task--------------------
    public void createTask(Task task){
        taskJpa.create(task);
    }
    
    public void deleteTask(int id){
        try {
            taskJpa.destroy(id);
        }
        catch (NonexistentEntityException ex) {
            
        }
    }
    
    public Task findTask(int id){
        return taskJpa.findTask(id);
    }
    
    public ArrayList<Task> findAllTasks(){
        List<Task> newList = taskJpa.findTaskEntities();
        ArrayList<Task> listToReturn =  new ArrayList<>(newList); 
        return listToReturn;
    }
    
    public void editTask(Task task){
        try {
            taskJpa.edit(task);
        }
        catch (Exception ex) {
            
        }
    }
}
