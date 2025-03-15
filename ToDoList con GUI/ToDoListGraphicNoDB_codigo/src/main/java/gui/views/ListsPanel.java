
package gui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import javax.swing.*;
import logic.*;

/**
 *
 * @author galax
 */
public class ListsPanel extends javax.swing.JPanel {

    /**
     * Creates new form Listas
     */
    User currentUser;
    LogicController logicController;
    final int spaceBetweenLists = 10;
    Point lastListPos = new Point(20,20);
    Point lastButtonPos = new Point(50,40);
    ArrayList<GList> lists = new ArrayList<>();
    public JButton listButton;
    public GList biggestList;
    boolean firstList;
    int listQuantity = 0;
    Font rockwellStriked;
    
    public ListsPanel(User currentUser, LogicController logicController) {
        initComponents();
        this.logicController = logicController;
        createRockwellStriked(14);
        this.currentUser = currentUser;
        firstList = true;
        biggestList = null;
        listButton = initButton();
        listsPanel.revalidate();
        listsPanel.repaint();
    }
    
    public User getCurrentUser(){
        return currentUser;
    }
    
    public void createAllLists(){
        Thread hilo = new Thread(() -> {
            if(currentUser.hasLists()){
                for(ListTask list : currentUser.getListOfLists()){
                    addList(list);
                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException ex) {
                        
                    }

                }
            }
            this.revalidate();
            this.repaint();
        });
        hilo.start();
       
    }
    
    private void createRockwellStriked(int tam){
        Font font = new Font("rockwell",Font.PLAIN, tam );
        Map fontAttr = font.getAttributes();
        fontAttr.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
        this.rockwellStriked = new Font(fontAttr);
    }
    
    public Font getRockwellStriked(){
        return this.rockwellStriked;
    }
    
    public void changePanelSize(Dimension newDimension){
        listsPanel.setPreferredSize(newDimension);
    }
    
    public Dimension getPanelDimension(){
        return listsPanel.getPreferredSize();
    }
    
    public GList getBiggestList(){
        return this.biggestList;
    }
    
    public void setBiggestList(GList newBiggestList){
        this.biggestList = newBiggestList;
    }
    
    private JButton initButton(){
        JButton listButton = new JButton();
        listButton.setVisible(true);
        listButton.setIcon(new ImageIcon(this.getClass().getResource("/icons/Plus.png")));
        listButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        listButton.setSize(new java.awt.Dimension(50, 50));
        listButton.setBackground(Color.white);
        

        listButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if(listButton.isEnabled() && SwingUtilities.isLeftMouseButton(evt)){
                    
                    addList();
                    lastButtonPos.x += GList.listWidth + spaceBetweenLists;
                    listButton.setLocation(lastButtonPos);
                    listsPanel.revalidate();
                    listsPanel.repaint();
                    
                    
                    
                }
                
            }
        });
        
        
        
        listButton.setLocation(lastButtonPos);
        
        

        listsPanel.add(listButton);
        listsPanel.revalidate();
        listsPanel.repaint();
        
        return listButton;
    }
    
    public void setButon(boolean bool){
        listButton.setEnabled(bool);
    }
    
    public void disableAll(GList alist){
        for(GList list : lists){
    
            if(alist.equals(list)){
                list.disableWihtoutEdit();
            }
            else{
                list.disable();
            }
            
                
        }
    }
    
    public void enableAll(){
        for(GList list : lists){
            list.enable();
        }
    }
    
    public void saveUserInDB(){
        logicController.editUser(currentUser);
    }
    
    private void addList(){
        GList newList = new GList(this, this.logicController);
        currentUser.addNewList(newList.getLogicList());
        newList.setLocation(lastListPos);
        lastListPos.x += GList.listWidth + spaceBetweenLists;
        listsPanel.add(newList);
        
        listQuantity++;
        if(listQuantity*GList.listWidth + 50 + 50 + listQuantity*spaceBetweenLists > listsPanel.getWidth()){
            changePanelSize(new Dimension(listsPanel.getWidth() + GList.listWidth + 15,listsPanel.getHeight()));
        }
        listsPanel.revalidate();
        listsPanel.repaint();
        lists.add(newList);
        if(firstList){
            biggestList = newList;
            firstList = false;
        }
        logicController.editUser(currentUser);

    }
    
    private void addList(ListTask newLogicList){
        GList newList = new GList(this, this.logicController, newLogicList);
        newList.setLocation(lastListPos);
        lastListPos.x += GList.listWidth + spaceBetweenLists;
        listsPanel.add(newList);
        
        
        listQuantity++;
        if(listQuantity*GList.listWidth + 50 + 50 + listQuantity*spaceBetweenLists > listsPanel.getWidth()){
            listsPanel.setPreferredSize(new Dimension(listsPanel.getWidth() + GList.listWidth + spaceBetweenLists + 5,listsPanel.getHeight()));
        }
        listsPanel.revalidate();
        listsPanel.repaint();
        lists.add(newList);
        if(firstList){
            biggestList = newList;
            firstList = false;
        }
        
        lastButtonPos.x += GList.listWidth + spaceBetweenLists;
        listButton.setLocation(lastButtonPos);
        newList.createAllTasks();
        listsPanel.revalidate();
        listsPanel.repaint();

    }
    
    private GList getListWithMaxHeight(){
        ArrayList<GList> listSort = (ArrayList < GList >) lists.clone();
        Comparator<GList> withHeight = Comparator.comparingInt(GList::getHeight);
        listSort.sort(withHeight);
        
        return listSort.get(listSort.size()-1);
        
    }
    
    public void setPanelToBiggestList(){
        biggestList = getListWithMaxHeight();
        listsPanel.setPreferredSize(new Dimension(listsPanel.getPreferredSize().width, biggestList.getHeight() + biggestList.getY() + 20));
    }
    
    public void deleteList(GList listToDelete){
        lastButtonPos.x -= (GList.listWidth + spaceBetweenLists);
        lastListPos.x -= (GList.listWidth + spaceBetweenLists);
        listButton.setLocation(lastButtonPos);
        
        int i = lists.indexOf(listToDelete);
        for(i++; i<lists.size(); i++){
            GList panel1 = lists.get(i);
            panel1.setLocation(panel1.getX() - (GList.listWidth + spaceBetweenLists), panel1.getY());
        }
        lists.remove(listToDelete);
        listToDelete.setVisible(false);
        if(listQuantity*GList.listWidth + 50 + 50 + listQuantity*spaceBetweenLists < listsPanel.getWidth()){
            changePanelSize(new Dimension(listsPanel.getWidth() - GList.listWidth - spaceBetweenLists,listsPanel.getHeight()));
        }
        listQuantity--;
        
        
        listsPanel.remove(listToDelete);
        if(listToDelete.equals(biggestList)){
            if(!lists.isEmpty()){
                biggestList = getListWithMaxHeight();
                listsPanel.setPreferredSize(new Dimension(listsPanel.getPreferredSize().width, biggestList.getHeight() + biggestList.getY() + 20));
            }
            else{
                biggestList = null;
                listsPanel.setPreferredSize(new Dimension(listsPanel.getPreferredSize().width, 1));
            }
        }
        currentUser.removeList(listToDelete.getLogicList());
        listToDelete.deleteListFromDB();
        saveUserInDB();
        listsPanel.revalidate();
        listsPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        listsPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(239, 239, 239));

        listsPanel.setBackground(new java.awt.Color(0, 0, 204));

        javax.swing.GroupLayout listsPanelLayout = new javax.swing.GroupLayout(listsPanel);
        listsPanel.setLayout(listsPanelLayout);
        listsPanelLayout.setHorizontalGroup(
            listsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 712, Short.MAX_VALUE)
        );
        listsPanelLayout.setVerticalGroup(
            listsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 584, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(listsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel listsPanel;
    // End of variables declaration//GEN-END:variables
}
