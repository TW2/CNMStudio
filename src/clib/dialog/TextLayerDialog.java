/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.dialog;

import clib.io.AudioFX;
import clib.layer.TextLayer.ISO_3166;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Yves
 */
public class TextLayerDialog extends javax.swing.JDialog {

    private ButtonPressed bp;
    private Frame parent = null;
    DefaultComboBoxModel comboFontModel = new DefaultComboBoxModel();
    DefaultComboBoxModel comboStyleModel = new DefaultComboBoxModel();
    SpinnerNumberModel spinSizeModel = new SpinnerNumberModel(12, 6, 600, 1);
    DefaultComboBoxModel comboISO3166Model = new DefaultComboBoxModel();
    DefaultTableModel tableISO3166Model = null;
    private Map<ISO_3166,String> text = new HashMap<>();
    //Audio au = new Audio(); //TODO
    AudioFX afx = new AudioFX();
    
    public enum ButtonPressed{
        NONE, OK_BUTTON, CANCEL_BUTTON;
    }
    
    public enum FontStyle{
        Plain(Font.PLAIN, "Plain"), 
        Italic(Font.ITALIC, "Italic"),
        Bold(Font.BOLD, "Bold"), 
        BoldItalic(Font.BOLD+Font.ITALIC, "BoldItalic");
        
        int style = Font.PLAIN;
        String name = "Plain";
        
        FontStyle(int style, String name){
            this.style = style;
            this.name = name;
        }
        
        @Override
        public String toString(){
            return name;
        }
        
        public int getStyle(){
            return style;
        }
    }
    
    /**
     * Creates new form TextLayerDialog
     * @param parent
     * @param modal
     */
    public TextLayerDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.parent = parent;
        bp = ButtonPressed.NONE;
        
        init();
    }
    
    private void init(){
        cbFont.setModel(comboFontModel);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        for(String family : fontFamilies){
            comboFontModel.addElement(family);
        }
        
        cbFontStyle.setModel(comboStyleModel);
        for(FontStyle fs : FontStyle.values()){
            comboStyleModel.addElement(fs);
        }        
        
        spinFontSize.setModel(spinSizeModel);
        
        cbISO3166.setModel(comboISO3166Model);
        for(ISO_3166 country : ISO_3166.values()){
            comboISO3166Model.addElement(country);
        }
        
        String[] head = new String[]{"Language", "Translation"};
        tableISO3166Model = new DefaultTableModel(
                null,
                head
        ){
            Class[] types = new Class [] {
                    ISO_3166.class, String.class};
            boolean[] canEdit = new boolean [] {
                    false, true};
            @Override
            public Class getColumnClass(int columnIndex) {return types [columnIndex];}
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        };
        ISO3166Table.setModel(tableISO3166Model);        
        TableColumn column;
        for (int i = 0; i < 2; i++) {
            column = ISO3166Table.getColumnModel().getColumn(i);
            switch(i){
                case 0:
                    column.setPreferredWidth(30);
                    break; //Language
                case 1:
                    column.setPreferredWidth(30);
                    break; //Translation
            }
        }
        
//        au.setRemotePauseButton(btnPause);
//        au.setRemotePlayButton(btnPlay);
//        au.setRemoteRecButton(btnRecord);
//        au.setRemoteStopButton(btnStop);
    }
    
    public boolean showDialog(){
        setLocationRelativeTo(null);
        setVisible(true);
        return bp==ButtonPressed.OK_BUTTON;
    }
    
    public void setTexts(Map<ISO_3166,String> text){
        this.text = text;
        for(ISO_3166 country : text.keySet()){
            Object[] row = {country, text.get(country)};
            tableISO3166Model.addRow(row);
        }
    }
    
    public Map<ISO_3166,String> getTexts(){
        return text;
    }
    
    public void setTextColor(Color color){
        lblColor.setBackground(color);
    }
    
    public Color getTextColor(){
        return lblColor.getBackground();
    }
    
    public void setTextFont(Font font){
        cbFont.setSelectedItem(font.getFamily());
        if(font.getStyle()==Font.PLAIN){
            cbFontStyle.setSelectedItem(FontStyle.Plain);
        }else if(font.getStyle()==Font.ITALIC){
            cbFontStyle.setSelectedItem(FontStyle.Italic);
        }else if(font.getStyle()==Font.BOLD){
            cbFontStyle.setSelectedItem(FontStyle.Bold);
        }else if(font.getStyle()==Font.BOLD+Font.ITALIC){
            cbFontStyle.setSelectedItem(FontStyle.BoldItalic);
        }        
        spinFontSize.setValue(font.getSize());
    }
    
    public Font getTextFont(){
        return new Font((String)cbFont.getSelectedItem(), ((FontStyle)cbFontStyle.getSelectedItem()).getStyle(), (int)spinFontSize.getValue());
    }
    
    private String getMainDirectory(){
        if(System.getProperty("os.name").equalsIgnoreCase("Mac OS X")){
            java.io.File file = new java.io.File("");
            return file.getAbsolutePath();
        }
        String path = System.getProperty("user.dir");
        if(path.toLowerCase().contains("jre")){
            File f = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toString()
                    .substring(6));
            path = f.getParent();
        }
        return path;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cbISO3166 = new javax.swing.JComboBox();
        tfISO3166 = new javax.swing.JTextField();
        btnAddISO3166 = new javax.swing.JButton();
        btnDeleteISO3166 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        ISO3166Table = new javax.swing.JTable();
        btnModifyISO3166 = new javax.swing.JButton();
        btnRecord = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        lblColor = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbFont = new javax.swing.JComboBox();
        cbFontStyle = new javax.swing.JComboBox();
        spinFontSize = new javax.swing.JSpinner();
        OK_Button = new javax.swing.JButton();
        Cancel_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Text and audio"));

        cbISO3166.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        tfISO3166.setText("My text.");

        btnAddISO3166.setText("Add");
        btnAddISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddISO3166ActionPerformed(evt);
            }
        });

        btnDeleteISO3166.setText("Delete");
        btnDeleteISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteISO3166ActionPerformed(evt);
            }
        });

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        ISO3166Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(ISO3166Table);

        btnModifyISO3166.setText("Modify");
        btnModifyISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyISO3166ActionPerformed(evt);
            }
        });

        btnRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_record.png"))); // NOI18N
        btnRecord.setToolTipText("Record");
        btnRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecordActionPerformed(evt);
            }
        });

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_play.png"))); // NOI18N
        btnPlay.setToolTipText("Play");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_pause.png"))); // NOI18N
        btnPause.setToolTipText("Pause");
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_stop.png"))); // NOI18N
        btnStop.setToolTipText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cbISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnAddISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnModifyISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tfISO3166))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPlay, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPause, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(162, 162, 162))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnStop, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPause, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPlay, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(tfISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddISO3166)
                            .addComponent(btnModifyISO3166)
                            .addComponent(btnDeleteISO3166)
                            .addComponent(cbISO3166, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnRecord, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Color"));

        lblColor.setBackground(new java.awt.Color(0, 0, 0));
        lblColor.setText(" ");
        lblColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColor.setOpaque(true);
        lblColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblColor, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Font"));

        cbFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbFontStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(cbFont, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(cbFontStyle, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spinFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(cbFont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbFontStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinFontSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        OK_Button.setText("OK");
        OK_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OK_ButtonActionPerformed(evt);
            }
        });

        Cancel_Button.setText("Cancel");
        Cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Cancel_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OK_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OK_Button)
                    .addComponent(Cancel_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorMouseClicked
        lblColor.setBackground(JColorChooser.showDialog(parent, "Choose a color", lblColor.getBackground()));
    }//GEN-LAST:event_lblColorMouseClicked

    private void OK_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OK_ButtonActionPerformed
        bp = ButtonPressed.OK_BUTTON;
        dispose();
    }//GEN-LAST:event_OK_ButtonActionPerformed

    private void Cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel_ButtonActionPerformed
        bp = ButtonPressed.CANCEL_BUTTON;
        dispose();
    }//GEN-LAST:event_Cancel_ButtonActionPerformed

    private void btnAddISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddISO3166ActionPerformed
        Object[] row = {(ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText()};
        tableISO3166Model.addRow(row);
        text.put((ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText());
    }//GEN-LAST:event_btnAddISO3166ActionPerformed

    private void btnDeleteISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteISO3166ActionPerformed
        try{
            int tabtemp[] = ISO3166Table.getSelectedRows();
            for (int i=tabtemp.length-1;i>=0;i--){
                text.remove((ISO_3166)ISO3166Table.getValueAt(tabtemp[i], 0));
                tableISO3166Model.removeRow(tabtemp[i]);
            }
        }catch(Exception exc){}
    }//GEN-LAST:event_btnDeleteISO3166ActionPerformed

    private void btnModifyISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyISO3166ActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            ISO3166Table.setValueAt(tfISO3166.getText(), ISO3166Table.getSelectedRow(), 1);
            text.put((ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText());
        }
    }//GEN-LAST:event_btnModifyISO3166ActionPerformed

    private void btnRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecordActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            ISO_3166 code = (ISO_3166)ISO3166Table.getValueAt(ISO3166Table.getSelectedRow(), 0);
            File file = new File(getMainDirectory()+File.separator+"temp");
            if(file.exists()==false){
                file.mkdir();
            }
            afx.setRecordPath("C:\\Users\\Phil\\Documents\\junk.wav");
            afx.recordStart();
        }
    }//GEN-LAST:event_btnRecordActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            ISO_3166 code = (ISO_3166)ISO3166Table.getValueAt(ISO3166Table.getSelectedRow(), 0);
//            au.setPlayFile(getMainDirectory()+File.separator+"temp"+File.separator+code.getAlpha3()+".ogg");
//            au.setPlayFile(getMainDirectory()+File.separator+"temp"+File.separator+code.getAlpha3()+".wav");
//            au.startPlaying();
            afx.setListenPath("C:\\Users\\Phil\\Documents\\aaa.wav");
            afx.listenStart();
            try {
                afx.viewMixerInfo();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(TextLayerDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
//            au.pausePlaying();
        }
    }//GEN-LAST:event_btnPauseActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            afx.listenStop();
            afx.recordStop();
        }
    }//GEN-LAST:event_btnStopActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextLayerDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TextLayerDialog dialog = new TextLayerDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Cancel_Button;
    private javax.swing.JTable ISO3166Table;
    private javax.swing.JButton OK_Button;
    private javax.swing.JButton btnAddISO3166;
    private javax.swing.JButton btnDeleteISO3166;
    private javax.swing.JButton btnModifyISO3166;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnRecord;
    private javax.swing.JButton btnStop;
    private javax.swing.JComboBox cbFont;
    private javax.swing.JComboBox cbFontStyle;
    private javax.swing.JComboBox cbISO3166;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblColor;
    private javax.swing.JSpinner spinFontSize;
    private javax.swing.JTextField tfISO3166;
    // End of variables declaration//GEN-END:variables
}
