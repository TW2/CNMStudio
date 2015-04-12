/*
 * Copyright (C) 2015 Antoine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package clib.dialog;

import clib.filter.AACFilter;
import clib.filter.AudioFilter;
import clib.filter.M4AFilter;
import clib.filter.MP3Filter;
import clib.io.AudioFX;
import clib.layer.AudioLayer;
import clib.layer.FontLayer;
import clib.layer.TextLayer.ISO_3166;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Antoine
 */
public class TextLayerWithAudioDialog extends javax.swing.JDialog {

    private ButtonPressed bp = ButtonPressed.NONE;
    Frame parent = null;
    DefaultComboBoxModel comboFontModel = new DefaultComboBoxModel();
    DefaultComboBoxModel comboStyleModel = new DefaultComboBoxModel();
    SpinnerNumberModel spinSizeModel = new SpinnerNumberModel(12, 6, 600, 1);
    DefaultComboBoxModel comboISO3166Model = new DefaultComboBoxModel();
    private DefaultTableModel tableISO3166Model = null;
    private Map<ISO_3166,String> text = new HashMap<>();
    private Map<ISO_3166,FontLayer> display = new HashMap<>();
    private Map<ISO_3166,AudioLayer> audio = new HashMap<>();
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
     * Creates new form TextLayerWithAudioDialog
     * @param parent
     * @param modal
     */
    public TextLayerWithAudioDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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
        
        String[] head = new String[]{"Language", "Translation", "Display", "Audio"};
        tableISO3166Model = new DefaultTableModel(
                null,
                head
        ){
            Class[] types = new Class [] {
                    ISO_3166.class, String.class, FontLayer.class, AudioLayer.class};
            boolean[] canEdit = new boolean [] {
                    false, true, false, false};
            @Override
            public Class getColumnClass(int columnIndex) {return types [columnIndex];}
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {return canEdit [columnIndex];}
        };
        ISO3166Table.setModel(tableISO3166Model);        
        TableColumn column;
        for (int i = 0; i < 4; i++) {
            column = ISO3166Table.getColumnModel().getColumn(i);
            switch(i){
                case 0:
                    column.setPreferredWidth(30);
                    break; //Language
                case 1:
                    column.setPreferredWidth(30);
                    break; //Translation
                case 2:
                    column.setPreferredWidth(30);
                    break; //Display
                case 3:
                    column.setPreferredWidth(30);
                    break; //Audio
            }
        }
    }
    
    public boolean showDialog(){
        setLocationRelativeTo(null);
        setVisible(true);
        return bp==ButtonPressed.OK_BUTTON;
    }
    
    public void setUP(Map<ISO_3166,String> text, Map<ISO_3166,FontLayer> fo, Map<ISO_3166,AudioLayer> au){
        this.text = text;
        this.display = fo;
        this.audio = au;
        for(ISO_3166 country : text.keySet()){
            Object[] row = {country, text.get(country), fo.getOrDefault(country, new FontLayer()), au.getOrDefault(country, new AudioLayer())};
            tableISO3166Model.addRow(row);
        }
    }
    
    public Map<ISO_3166,String> getTexts(){
        return text;
    }
    
    public Map<ISO_3166,FontLayer> getDisplays(){
        return display;
    }
    
    public Map<ISO_3166,AudioLayer> getAudios(){
        return audio;
    }
    
    public void setTextColor(Color color){
        lblColor.setBackground(color);
        tfDisplayText.setForeground(color);
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
        btnModifyISO3166 = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        btnPause = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        tfAudioPath = new javax.swing.JTextField();
        btnAudoPath = new javax.swing.JButton();
        tfAudioName = new javax.swing.JTextField();
        lblColor = new javax.swing.JLabel();
        cbFont = new javax.swing.JComboBox();
        cbFontStyle = new javax.swing.JComboBox();
        spinFontSize = new javax.swing.JSpinner();
        tfDisplayText = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ISO3166Table = new javax.swing.JTable();
        Cancel_Button = new javax.swing.JButton();
        OK_Button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1040, 555));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Text, audio, font and color"));
        jPanel1.setLayout(null);

        cbISO3166.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cbISO3166);
        cbISO3166.setBounds(10, 100, 224, 30);

        tfISO3166.setText("My text.");
        tfISO3166.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfISO3166KeyReleased(evt);
            }
        });
        jPanel1.add(tfISO3166);
        tfISO3166.setBounds(10, 20, 500, 30);

        btnAddISO3166.setText("Add");
        btnAddISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddISO3166ActionPerformed(evt);
            }
        });
        jPanel1.add(btnAddISO3166);
        btnAddISO3166.setBounds(240, 100, 85, 30);

        btnDeleteISO3166.setText("Delete");
        btnDeleteISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteISO3166ActionPerformed(evt);
            }
        });
        jPanel1.add(btnDeleteISO3166);
        btnDeleteISO3166.setBounds(420, 100, 85, 30);

        btnModifyISO3166.setText("Modify");
        btnModifyISO3166.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyISO3166ActionPerformed(evt);
            }
        });
        jPanel1.add(btnModifyISO3166);
        btnModifyISO3166.setBounds(330, 100, 85, 30);

        btnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_play.png"))); // NOI18N
        btnPlay.setToolTipText("Play");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });
        jPanel1.add(btnPlay);
        btnPlay.setBounds(510, 20, 43, 70);

        btnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_pause.png"))); // NOI18N
        btnPause.setToolTipText("Pause");
        btnPause.setEnabled(false);
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });
        jPanel1.add(btnPause);
        btnPause.setBounds(610, 20, 43, 70);

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/xsmall_stop.png"))); // NOI18N
        btnStop.setToolTipText("Stop");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        jPanel1.add(btnStop);
        btnStop.setBounds(560, 20, 43, 70);

        tfAudioPath.setToolTipText("Choose audio...");
        jPanel1.add(tfAudioPath);
        tfAudioPath.setBounds(10, 60, 315, 30);

        btnAudoPath.setText("...");
        btnAudoPath.setToolTipText("Choose an AAC or M4A audio file...");
        btnAudoPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAudoPathActionPerformed(evt);
            }
        });
        jPanel1.add(btnAudoPath);
        btnAudoPath.setBounds(420, 60, 85, 30);

        tfAudioName.setText("ID");
        tfAudioName.setToolTipText("Choose a name for the audio");
        jPanel1.add(tfAudioName);
        tfAudioName.setBounds(330, 60, 85, 30);

        lblColor.setBackground(new java.awt.Color(0, 0, 0));
        lblColor.setText(" ");
        lblColor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblColor.setOpaque(true);
        lblColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblColorMouseClicked(evt);
            }
        });
        jPanel1.add(lblColor);
        lblColor.setBounds(900, 60, 130, 30);

        cbFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFontActionPerformed(evt);
            }
        });
        jPanel1.add(cbFont);
        cbFont.setBounds(660, 20, 370, 30);

        cbFontStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbFontStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFontStyleActionPerformed(evt);
            }
        });
        jPanel1.add(cbFontStyle);
        cbFontStyle.setBounds(660, 60, 110, 30);

        spinFontSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinFontSizeStateChanged(evt);
            }
        });
        jPanel1.add(spinFontSize);
        spinFontSize.setBounds(780, 60, 110, 30);

        tfDisplayText.setEditable(false);
        tfDisplayText.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfDisplayText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfDisplayText.setText("My text.");
        jPanel1.add(tfDisplayText);
        tfDisplayText.setBounds(10, 140, 1020, 70);

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

        jPanel1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 220, 1020, 280);

        Cancel_Button.setText("Cancel");
        Cancel_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cancel_ButtonActionPerformed(evt);
            }
        });

        OK_Button.setText("OK");
        OK_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OK_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(824, Short.MAX_VALUE)
                .addComponent(Cancel_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(OK_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(OK_Button)
                    .addComponent(Cancel_Button))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddISO3166ActionPerformed
        FontLayer fl = FontLayer.create(getTextFont(), lblColor.getBackground());
        AudioLayer al = AudioLayer.create(tfAudioName.getText(), tfAudioPath.getText());
        Object[] row = {(ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText(), fl, al};
        tableISO3166Model.addRow(row);
        text.put((ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText());
        display.put((ISO_3166)cbISO3166.getSelectedItem(), fl);
        audio.put((ISO_3166)cbISO3166.getSelectedItem(), al);
    }//GEN-LAST:event_btnAddISO3166ActionPerformed

    private void btnDeleteISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteISO3166ActionPerformed
        try{
            int tabtemp[] = ISO3166Table.getSelectedRows();
            for (int i=tabtemp.length-1;i>=0;i--){
                text.remove((ISO_3166)ISO3166Table.getValueAt(tabtemp[i], 0));
                display.remove((ISO_3166)ISO3166Table.getValueAt(tabtemp[i], 0));
                audio.remove((ISO_3166)ISO3166Table.getValueAt(tabtemp[i], 0));
                tableISO3166Model.removeRow(tabtemp[i]);
            }
        }catch(Exception exc){}
    }//GEN-LAST:event_btnDeleteISO3166ActionPerformed

    private void btnModifyISO3166ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyISO3166ActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            FontLayer fl = FontLayer.create(getTextFont(), lblColor.getBackground());
            AudioLayer al = AudioLayer.create(tfAudioName.getText(), tfAudioPath.getText());
            ISO3166Table.setValueAt(tfISO3166.getText(), ISO3166Table.getSelectedRow(), 1);
            ISO3166Table.setValueAt(fl, ISO3166Table.getSelectedRow(), 2);
            ISO3166Table.setValueAt(al, ISO3166Table.getSelectedRow(), 3);
            text.put((ISO_3166)cbISO3166.getSelectedItem(), tfISO3166.getText());
            display.put((ISO_3166)cbISO3166.getSelectedItem(), fl);
            audio.put((ISO_3166)cbISO3166.getSelectedItem(), al);            
        }
    }//GEN-LAST:event_btnModifyISO3166ActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        if(ISO3166Table.getSelectedRow() != -1){
            AudioLayer al = (AudioLayer)ISO3166Table.getValueAt(ISO3166Table.getSelectedRow(), 3);
            if(al.getAACPath().isEmpty() == false){
                afx.setListenPath(al.getAACPath());
                afx.listenStart();
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
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void lblColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblColorMouseClicked
        lblColor.setBackground(JColorChooser.showDialog(parent, "Choose a color", lblColor.getBackground()));
        tfDisplayText.setForeground(lblColor.getBackground());
    }//GEN-LAST:event_lblColorMouseClicked

    private void Cancel_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cancel_ButtonActionPerformed
        bp = ButtonPressed.CANCEL_BUTTON;
        dispose();
    }//GEN-LAST:event_Cancel_ButtonActionPerformed

    private void OK_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OK_ButtonActionPerformed
        bp = ButtonPressed.OK_BUTTON;
        dispose();
    }//GEN-LAST:event_OK_ButtonActionPerformed

    private void cbFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFontActionPerformed
        try{
            Font f = getTextFont();
            tfDisplayText.setFont(f);
        }catch (Exception ex){
           
        }        
    }//GEN-LAST:event_cbFontActionPerformed

    private void cbFontStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFontStyleActionPerformed
        try{
            Font f = getTextFont();
            tfDisplayText.setFont(f);
        }catch (Exception ex){
           
        }
    }//GEN-LAST:event_cbFontStyleActionPerformed

    private void spinFontSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinFontSizeStateChanged
        try{
            Font f = getTextFont();
            tfDisplayText.setFont(f);
        }catch (Exception ex){
           
        }
    }//GEN-LAST:event_spinFontSizeStateChanged

    private void btnAudoPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAudoPathActionPerformed
        JFileChooser fc = new JFileChooser();
        for (FileFilter f : fc.getChoosableFileFilters()){
                fc.removeChoosableFileFilter(f);
            }
            fc.setDialogTitle("Choose an MP3/AAC/M4A sound...");
            fc.setDialogType(JFileChooser.OPEN_DIALOG);
            fc.addChoosableFileFilter(new MP3Filter());
            fc.addChoosableFileFilter(new AACFilter());
            fc.addChoosableFileFilter(new M4AFilter());
            fc.addChoosableFileFilter(new AudioFilter());
            int z = fc.showOpenDialog(this);
            if (z == JFileChooser.APPROVE_OPTION){
                tfAudioPath.setText(fc.getSelectedFile().getAbsolutePath());
            }
    }//GEN-LAST:event_btnAudoPathActionPerformed

    private void tfISO3166KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfISO3166KeyReleased
        tfDisplayText.setText(tfISO3166.getText());
    }//GEN-LAST:event_tfISO3166KeyReleased

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TextLayerWithAudioDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TextLayerWithAudioDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TextLayerWithAudioDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TextLayerWithAudioDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                TextLayerWithAudioDialog dialog = new TextLayerWithAudioDialog(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btnAudoPath;
    private javax.swing.JButton btnDeleteISO3166;
    private javax.swing.JButton btnModifyISO3166;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStop;
    private javax.swing.JComboBox cbFont;
    private javax.swing.JComboBox cbFontStyle;
    private javax.swing.JComboBox cbISO3166;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblColor;
    private javax.swing.JSpinner spinFontSize;
    private javax.swing.JTextField tfAudioName;
    private javax.swing.JTextField tfAudioPath;
    private javax.swing.JTextField tfDisplayText;
    private javax.swing.JTextField tfISO3166;
    // End of variables declaration//GEN-END:variables
}
