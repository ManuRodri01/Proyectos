/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui.views;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import logic.*;

/**
 *
 * @author galax
 */
public class GList extends javax.swing.JPanel {

    
    ListTask logicList;
    LogicController logicController;
    final int flowVeticalGap = 5;
    static public final int listWidth = 360;
    public ListsPanel panelOfLists;
    ArrayList<GTask> tasks;
    ArrayList<Task> logicTasks;
    
    int id;
    public GList(ListsPanel panelOfLists, LogicController logicController) {
        initComponents();
        this.logicController = logicController;
        logicTasks = new ArrayList<>();
        this.logicList = new ListTask("",logicTasks, panelOfLists.getCurrentUser());
        this.id = logicList.getId();
        tasks = new ArrayList<>();
        this.panelOfLists = panelOfLists;
        btnDelete.setEnabled(false);
        for(int i = 0; i < taskPanel.getComponentCount(); i++){
            taskPanel.getComponent(i).setEnabled(false);
        }
        panelOfLists.setButon(false);
        panelOfLists.disableAll(this);
        logicController.createList(logicList);
        this.setSize(GList.listWidth,130);
        
        
    }
    
    public GList(ListsPanel panelOfLists, LogicController logicController, ListTask existingLogicList) {
        initComponents();
        this.logicController = logicController;
        this.logicList = existingLogicList;
        this.logicTasks = existingLogicList.getList();
        this.id = existingLogicList.getId();
        tasks = new ArrayList<>();
        this.panelOfLists = panelOfLists;
        this.setSize(GList.listWidth,130);
        btnEdit.setSelected(false);
        btnEdit.setIcon(new ImageIcon(this.getClass().getResource("/icons/edit.png")));
        completeName.setVisible(false);
        listName.setText(existingLogicList.getListName());
        completeName.setText(existingLogicList.getListName());
        listName.setVisible(true);
        
        
        
        
        
        
    }
    
    public void createAllTasks(){
        
        if(logicList.hasTasks()){
            for(Task task : logicList.getList()){
                addElement(taskPanel, task);

            }
            
        }
        
    }
    
    
    public ListTask getLogicList(){
        return logicList;
    }
    
    public void saveListTaskInDB(){
        logicController.editList(logicList);
    }
    public void saveUserInDB(){
        panelOfLists.saveUserInDB();
    }
    
    public void deleteListFromDB(){
        logicController.deleteList(id);
    }
    
    public void disable(){
        btnDelete.setEnabled(false);
        btnEdit.setEnabled(false);
        for(int i = 0; i < taskPanel.getComponentCount(); i++){
            taskPanel.getComponent(i).setEnabled(false);
        }
        
    }
    public void disableWihtoutEdit(){
        btnDelete.setEnabled(false);
        for(int i = 0; i < taskPanel.getComponentCount(); i++){
            taskPanel.getComponent(i).setEnabled(false);
        }
    }
    
    public Font getRockwellStriked(){
        return panelOfLists.getRockwellStriked();
    }
    
    public void setPanelOfListsToBiggest(){
        panelOfLists.setPanelToBiggestList();
    }
    
    public void setThisListToBiggest(){
        panelOfLists.setBiggestList(this);
    }
    
    public void revalidatePanelOfLists(){
        panelOfLists.revalidate();
    }
    
    public void repaintPanelOfLists(){
        panelOfLists.repaint();
    }
    
    public GList getBiggestList(){
        return panelOfLists.getBiggestList();
    }
    
    public Dimension getBigPanelDimension(){
        return panelOfLists.getPanelDimension();
    }
    
    public void changeBigPanelSize(Dimension newDimension){
        panelOfLists.changePanelSize(newDimension);
    }
    
    public void enableAllList(){
        panelOfLists.enableAll();
    }
    
    public void disableAllLists(){
        panelOfLists.disableAll(this);
    }
    
    public void setListsButon(boolean bool){
        panelOfLists.setButon(bool);
    }
    
    
    public void enable(){
        btnDelete.setEnabled(true);
        btnEdit.setEnabled(true);
        for(int i = 0; i < taskPanel.getComponentCount(); i++){
            taskPanel.getComponent(i).setEnabled(true);
        }
    }
    
    private void addElement(JPanel panelToAddElement){
        GTask element = new GTask(this, this.logicController);
        jButton1.setVisible(false);
        taskPanel.remove(jButton1);
        logicTasks.add(element.getLogicTask());
        logicList.setList(logicTasks);
        
        
        panelToAddElement.add(element);
        this.setSize(new Dimension(this.getWidth(), this.getHeight() + GTask.taskHeight + flowVeticalGap));
        
        
        if((this.getHeight() + this.getY()) > panelOfLists.getPanelDimension().height){
            panelOfLists.changePanelSize(new Dimension (panelOfLists.getPanelDimension().width, (this.getHeight() + this.getY()) + 20));
            panelOfLists.revalidate();
            panelOfLists.repaint();
            panelOfLists.setBiggestList(this);
        }
        
        tasks.add(element);
        panelToAddElement.add(jButton1);
        jButton1.setVisible(true);
        
        panelToAddElement.revalidate();
        panelToAddElement.repaint();
        logicController.editList(logicList);
        saveUserInDB();
        
        
    }
    
    private void addElement(JPanel panelToAddElement, Task newLogicTask){
        GTask element = new GTask(this, this.logicController, newLogicTask);
        jButton1.setVisible(false);
        taskPanel.remove(jButton1);
        
        panelToAddElement.add(element);
        //element.changeText(String.valueOf(element.getPreferredSize().height));
        this.setSize(new Dimension(this.getWidth(), this.getHeight() + element.getPreferredSize().height + flowVeticalGap));
        
        element.setVisible(true);
        if((this.getHeight() + this.getY()) > panelOfLists.getPanelDimension().height){
            panelOfLists.changePanelSize(new Dimension (panelOfLists.getPanelDimension().width, (this.getHeight() + this.getY()) + 20));
            panelOfLists.revalidate();
            panelOfLists.repaint();
            panelOfLists.setBiggestList(this);
        }
        
        tasks.add(element);
        panelToAddElement.add(jButton1);
        jButton1.setVisible(true);
        
        panelToAddElement.revalidate();
        panelToAddElement.repaint();
        
        
        
    }
    
    public void deleteElement(GTask taskToDelete){
        tasks.remove(taskToDelete);
        taskToDelete.setVisible(false);
        taskPanel.remove(taskToDelete);
        logicTasks.remove(taskToDelete.getLogicTask());
        logicList.setList(logicTasks);
        taskToDelete.deleteTaskFromDB();
        logicController.editList(logicList);
        saveUserInDB();
        this.setSize(this.getWidth(), this.getHeight() - taskToDelete.getPreferredSize().height-flowVeticalGap);
        if(this.equals(panelOfLists.getBiggestList())){
            panelOfLists.setPanelToBiggestList();
        }
        
        
        taskPanel.revalidate();
        taskPanel.repaint();
    }
    
    public void deleteAllTasksFromDB(){
        for(GTask task : tasks){
            task.deleteTaskFromDB();
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listTitle = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        completeName = new javax.swing.JTextField();
        listName = new javax.swing.JTextField();
        btnEdit = new javax.swing.JToggleButton();
        btnDelete = new javax.swing.JButton();
        taskPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(360, 300));

        listTitle.setBackground(new java.awt.Color(255, 255, 255));
        listTitle.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listTitle.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(34, 59, 282, 10));

        completeName.setBackground(new java.awt.Color(225, 225, 225));
        completeName.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        completeName.setForeground(new java.awt.Color(51, 51, 51));
        completeName.setText("  Inserte el nombre de su lista");
        completeName.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        completeName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                completeNameMousePressed(evt);
            }
        });
        completeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeNameActionPerformed(evt);
            }
        });
        listTitle.add(completeName, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 20, 230, 34));

        listName.setEditable(false);
        listName.setBackground(new java.awt.Color(255, 255, 255));
        listName.setFont(new java.awt.Font("Rockwell", 0, 14)); // NOI18N
        listName.setForeground(new java.awt.Color(0, 0, 0));
        listName.setBorder(null);
        listName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        listName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listNameActionPerformed(evt);
            }
        });
        listTitle.add(listName, new org.netbeans.lib.awtextra.AbsoluteConstraints(42, 20, 230, 34));

        btnEdit.setBackground(new java.awt.Color(255, 255, 255));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(this.getClass().getResource("/icons/check_white.png")));
        btnEdit.setSelected(true);
        btnEdit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEditMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEditMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btnEditMousePressed(evt);
            }
        });
        listTitle.add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 30, 20, 20));

        btnDelete.setBackground(new java.awt.Color(255, 255, 255));
        btnDelete.setIcon(new ImageIcon(this.getClass().getResource("/icons/trash.png")));
        btnDelete.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnDelete.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDelete.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnDeleteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDeleteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDeleteMouseExited(evt);
            }
        });
        listTitle.add(btnDelete, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 30, 20, 20));

        taskPanel.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("Agregar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        taskPanel.add(jButton1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(taskPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(listTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(taskPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void completeNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_completeNameActionPerformed

    private void listNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_listNameActionPerformed

    private void btnEditMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMousePressed
        if(btnEdit.isEnabled() && SwingUtilities.isLeftMouseButton(evt)){
            if(btnEdit.isSelected()){
               btnEdit.setIcon(new ImageIcon(this.getClass().getResource("/icons/edit.png")));
               completeName.setVisible(false);
               String name = completeName.getText();
               listName.setText(name); 
               panelOfLists.setButon(true);
               panelOfLists.enableAll();
               logicList.setListName(name);
                saveListTaskInDB();
            }
            else{
                btnEdit.setIcon(new ImageIcon(this.getClass().getResource("/icons/check_white.png")));
                completeName.setText(listName.getText());
                completeName.setVisible(true);
                panelOfLists.setButon(false);
                panelOfLists.disableAll(this);
                btnEdit.setEnabled(true);

            }
        }
        
        
    }//GEN-LAST:event_btnEditMousePressed

    private void btnDeleteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseClicked
        if(btnDelete.isEnabled() && SwingUtilities.isLeftMouseButton(evt)){
            deleteAllTasksFromDB();
            panelOfLists.deleteList(this);
        }
        
    }//GEN-LAST:event_btnDeleteMouseClicked

    private void btnDeleteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseEntered
        if(btnDelete.isEnabled())
        btnDelete.setBackground(Color.red);
    }//GEN-LAST:event_btnDeleteMouseEntered

    private void btnDeleteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnDeleteMouseExited
        if(btnDelete.isEnabled() )
        btnDelete.setBackground(Color.white);
    }//GEN-LAST:event_btnDeleteMouseExited

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        addElement(taskPanel);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        
        if(btnEdit.isEnabled()){
            if(btnEdit.isSelected()){
                btnEdit.setIcon(new ImageIcon(this.getClass().getResource("/icons/check_gray.png")));
            }
            btnEdit.setBackground(Color.lightGray);
        }
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        
        if(btnEdit.isEnabled()){
            if(btnEdit.isSelected()){
                btnEdit.setIcon(new ImageIcon(this.getClass().getResource("/icons/check_white.png")));
            }
            btnEdit.setBackground(Color.white);
        }

    }//GEN-LAST:event_btnEditMouseExited

    private void completeNameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_completeNameMousePressed
        if(completeName.getText().equals("  Inserte el nombre de su lista")){
            completeName.setText("");
        }
    }//GEN-LAST:event_completeNameMousePressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JToggleButton btnEdit;
    private javax.swing.JTextField completeName;
    private javax.swing.JButton jButton1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField listName;
    private javax.swing.JPanel listTitle;
    private javax.swing.JPanel taskPanel;
    // End of variables declaration//GEN-END:variables
}
