/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cnmstudio;

import clib.dialog.InputDialog;
import clib.dialog.ShapeLayerDialog;
import clib.dialog.TextLayerDialog;
import clib.filter.CNMFilter;
import clib.filter.ImageFilter;
import clib.io.DualLock;
import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.universe.LData;
import clib.universe.MainUniverse;
import cnmstudio.preview.ImagePreview;
import cnmstudio.renderer.LayerTreeRenderer;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Yves
 */
public class PageCreator extends javax.swing.JPanel {
    
    private Frame parent = null;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Pages");
//    private DefaultListModel listModel = new DefaultListModel();
    private final DefaultComboBoxModel comboFontModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel comboStyleModel = new DefaultComboBoxModel();
    private final SpinnerNumberModel spinSizeModel = new SpinnerNumberModel(12, 6, 600, 1);
    private final DefaultComboBoxModel comboLangModel = new DefaultComboBoxModel();
    private final DefaultTreeModel treePagesModel = new DefaultTreeModel(root);
    private final JScrollPane editorScrollPane = new JScrollPane();
    private static ShapeLayer lastSelectedShapeLayer = null;
    private static TextLayer lastSelectedTextLayer = null;
    private static PageLayer lastSelectedPageLayer = null;
    private PageLayer lastPageLayerReference = null;
    private final FontStyle fontstyle = FontStyle.Plain;
    
    private Page page = new Page();
    private LData LOL = new LData();
    private MainUniverse mu = new MainUniverse();
    

    /**
     * Creates new form PageCreator
     * @param parent
     */
    public PageCreator(Frame parent) {
        initComponents();
        this.parent = parent;
        init();
    }
    
    private void init(){
        try {
            javax.swing.UIManager.setLookAndFeel(new NimbusLookAndFeel());
            javax.swing.SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException exc) {
            System.out.println("Nimbus LookAndFeel not loaded : "+exc);
        }
        
        editorPanel.add(editorScrollPane);
        editorScrollPane.setVisible(false);
        
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
        
        cbAvailableLanguages.setModel(comboLangModel);
        
        jTree1.setModel(treePagesModel);
        jTree1.setCellRenderer(new LayerTreeRenderer());
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
    
    private void createImage(ImageIcon manga, ImageLayer imglay, PageLayer pl){
        BufferedImage bi = new BufferedImage(
                manga.getIconWidth(), 
                manga.getIconHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.drawImage(manga.getImage(), 0, 0, null);
        
        imglay.setImage(bi);
        
        page.setSize(manga.getIconWidth(), manga.getIconHeight());
        page.setPreferredSize(new Dimension(manga.getIconWidth(), manga.getIconHeight()));
        
        editorScrollPane.setSize(
                manga.getIconWidth()+20 > editorPanel.getWidth() ? editorPanel.getWidth() : manga.getIconWidth()+20, 
                manga.getIconHeight()+20> editorPanel.getHeight()? editorPanel.getHeight(): manga.getIconHeight()+20);
        
        editorScrollPane.setLocation( (editorPanel.getWidth() - editorScrollPane.getWidth())/2, 0);
        
        editorScrollPane.setViewportView(page);
        
        editorScrollPane.setVisible(true);
        
        //UPDATE 22/02/2015 - Add PageLayer to the function
        pl.setWidth(manga.getIconWidth());
        pl.setHeight(manga.getIconHeight());        
    }
    
    //UPDATE 22/02/2015
    private void updateImage(PageLayer pl){
        page.setSize(pl.getWidth(), pl.getHeight());
        page.setPreferredSize(new Dimension(pl.getWidth(), pl.getHeight()));
        
        editorScrollPane.setSize(
                pl.getWidth()+20 > editorPanel.getWidth() ? editorPanel.getWidth() : pl.getWidth()+20, 
                pl.getHeight()+20> editorPanel.getHeight()? editorPanel.getHeight(): pl.getHeight()+20);
        
        editorScrollPane.setLocation( (editorPanel.getWidth() - editorScrollPane.getWidth())/2, 0);
        
        editorScrollPane.setViewportView(page);
        editorScrollPane.setVisible(true);//Forcer la visibilité pour ouverture CNM - 23/02/2015
    }
    
    private void destroyImage(){
        editorScrollPane.setVisible(false);
    }
    
    public static ShapeLayer getLastShapeLayer(){
        return lastSelectedShapeLayer;
    }
    
    public static TextLayer getLastTextLayer(){
        return lastSelectedTextLayer;
    }
    
    private void searchForLanguage(){
        comboLangModel.removeAllElements();
        for(int i=0; i<jTree1.getRowCount(); i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getPathForRow(i).getLastPathComponent();
            if(node.getUserObject() instanceof TextLayer){
                TextLayer tl = (TextLayer)node.getUserObject();
                Set<TextLayer.ISO_3166> set = tl.getTextCountries();                
                for (TextLayer.ISO_3166 country : set) {
                    if(comboLangModel.getIndexOf(country) < 0){
                        comboLangModel.addElement(country);
                    }
                }
            }
        }
    }
    
    private void updateTree(){
        root.removeAllChildren();
        for(PageLayer pl : page.getPages()){
            DefaultMutableTreeNode pagenode = new DefaultMutableTreeNode(pl);
            root.add(pagenode);
            for(ImageLayer il : page.getImagesFrom(pl)){
                DefaultMutableTreeNode imagenode = new DefaultMutableTreeNode(il);
                pagenode.add(imagenode);
            }
            for(ShapeLayer sl : page.getShapesFrom(pl)){
                DefaultMutableTreeNode shapenode = new DefaultMutableTreeNode(sl);
                pagenode.add(shapenode);
            }
            for(TextLayer tl : page.getTextsFrom(pl)){
                DefaultMutableTreeNode textnode = new DefaultMutableTreeNode(tl);
                pagenode.add(textnode);
            }
        }
        jTree1.updateUI();
        for (int i=0; i<jTree1.getRowCount(); i++){
            jTree1.expandRow(i);
        }        
    }
    
    public void setLOL(LData LOL){
        this.LOL = LOL;
    }
    
    public LData getLOL(){
        return LOL;
    }
    
    public void setMainUniverse(MainUniverse mu){
        this.mu = mu;
    }
    
    public MainUniverse getMainUniverse(){
        return mu;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fcImage = new javax.swing.JFileChooser();
        fcXML = new javax.swing.JFileChooser();
        editorPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        btnAddPage = new javax.swing.JButton();
        btnAddImage = new javax.swing.JButton();
        btnAddShape = new javax.swing.JButton();
        btnAddText = new javax.swing.JButton();
        btnAddLine = new javax.swing.JButton();
        btnAddCurve = new javax.swing.JButton();
        cbFont = new javax.swing.JComboBox();
        cbFontStyle = new javax.swing.JComboBox();
        spinFontSize = new javax.swing.JSpinner();
        cbAvailableLanguages = new javax.swing.JComboBox();
        btnOpen = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnData = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        btnUndo = new javax.swing.JButton();
        btnRedo = new javax.swing.JButton();

        editorPanel.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout editorPanelLayout = new javax.swing.GroupLayout(editorPanel);
        editorPanel.setLayout(editorPanelLayout);
        editorPanelLayout.setHorizontalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 827, Short.MAX_VALUE)
        );
        editorPanelLayout.setVerticalGroup(
            editorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        controlPanel.setLayout(null);

        btnAddPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/raw64.png"))); // NOI18N
        btnAddPage.setToolTipText("Add a page");
        btnAddPage.setFocusable(false);
        btnAddPage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddPage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPageActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddPage);
        btnAddPage.setBounds(10, 80, 70, 70);

        btnAddImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/page64.png"))); // NOI18N
        btnAddImage.setToolTipText("Add an image");
        btnAddImage.setFocusable(false);
        btnAddImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddImage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddImageActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddImage);
        btnAddImage.setBounds(80, 80, 70, 70);

        btnAddShape.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/shape64.png"))); // NOI18N
        btnAddShape.setToolTipText("Add a shape");
        btnAddShape.setFocusable(false);
        btnAddShape.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddShape.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddShape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddShapeActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddShape);
        btnAddShape.setBounds(150, 80, 70, 70);

        btnAddText.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/text64.png"))); // NOI18N
        btnAddText.setToolTipText("Add a text");
        btnAddText.setFocusable(false);
        btnAddText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddText.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTextActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddText);
        btnAddText.setBounds(220, 80, 70, 70);

        btnAddLine.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/line.png"))); // NOI18N
        btnAddLine.setToolTipText("Add a line");
        btnAddLine.setFocusable(false);
        btnAddLine.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddLine.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddLineActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddLine);
        btnAddLine.setBounds(10, 150, 70, 70);

        btnAddCurve.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/curve.png"))); // NOI18N
        btnAddCurve.setToolTipText("Add a curve");
        btnAddCurve.setFocusable(false);
        btnAddCurve.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddCurve.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddCurve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCurveActionPerformed(evt);
            }
        });
        controlPanel.add(btnAddCurve);
        btnAddCurve.setBounds(80, 150, 70, 70);

        cbFont.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFontActionPerformed(evt);
            }
        });
        controlPanel.add(cbFont);
        cbFont.setBounds(150, 150, 140, 30);

        cbFontStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbFontStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbFontStyleActionPerformed(evt);
            }
        });
        controlPanel.add(cbFontStyle);
        cbFontStyle.setBounds(150, 190, 90, 30);

        spinFontSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinFontSizeStateChanged(evt);
            }
        });
        controlPanel.add(spinFontSize);
        spinFontSize.setBounds(240, 190, 50, 30);

        cbAvailableLanguages.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbAvailableLanguages.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAvailableLanguagesItemStateChanged(evt);
            }
        });
        controlPanel.add(cbAvailableLanguages);
        cbAvailableLanguages.setBounds(10, 230, 280, 30);

        btnOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/folder.png"))); // NOI18N
        btnOpen.setToolTipText("Open a document...");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });
        controlPanel.add(btnOpen);
        btnOpen.setBounds(150, 10, 70, 70);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/floppydisk.png"))); // NOI18N
        btnSave.setToolTipText("Save a document...");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        controlPanel.add(btnSave);
        btnSave.setBounds(220, 10, 70, 70);

        btnData.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/postit.png"))); // NOI18N
        btnData.setToolTipText("Data...");
        btnData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDataActionPerformed(evt);
            }
        });
        controlPanel.add(btnData);
        btnData.setBounds(80, 10, 70, 70);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/newdocument.png"))); // NOI18N
        btnNew.setToolTipText("Create a new document...");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        controlPanel.add(btnNew);
        btnNew.setBounds(10, 10, 70, 70);

        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
        });
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(jTree1);

        btnUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/32px-Crystal_Clear_action_back.png"))); // NOI18N
        btnUndo.setText("Undo");
        btnUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUndoActionPerformed(evt);
            }
        });

        btnRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/32px-Crystal_Clear_action_forward.png"))); // NOI18N
        btnRedo.setText("Redo");
        btnRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRedoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUndo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnRedo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRedo)
                    .addComponent(btnUndo)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(editorPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPageActionPerformed
        PageLayer pl = new PageLayer();
        
        InputDialog id = new InputDialog(parent, true);
        id.setDialogTitle("Page name");
        id.setDialogMessage("Please type a name for this page :");
        pl.setName(id.showDialog(pl.getName()));
        
        page.setActualPage(pl);
        
        page.getPages().add(pl);        
//        listModel.addElement(pl);
        updateTree();
    }//GEN-LAST:event_btnAddPageActionPerformed

    private void btnAddImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddImageActionPerformed
        if(lastPageLayerReference != null){
            ImageLayer il = new ImageLayer();
            ImageIcon manga;

            InputDialog id = new InputDialog(parent, true);
            id.setDialogTitle("Image name");
            id.setDialogMessage("Please type a name for this image :");
            il.setName(id.showDialog(il.getName()));

            for (FileFilter f : fcImage.getChoosableFileFilters()){
                fcImage.removeChoosableFileFilter(f);
            }
            fcImage.setDialogTitle("Open an image...");
            fcImage.setAccessory(new ImagePreview(fcImage));
            fcImage.setDialogType(JFileChooser.OPEN_DIALOG);
            fcImage.setFileFilter(new ImageFilter());
            int z = fcImage.showOpenDialog(this);
            if (z == JFileChooser.APPROVE_OPTION){
                manga = new ImageIcon(fcImage.getSelectedFile().getAbsolutePath());
                createImage(manga, il, lastPageLayerReference);
                lastPageLayerReference.getImages().add(il);
//                listModel.addElement(il);
            }
            updateTree();
        }
    }//GEN-LAST:event_btnAddImageActionPerformed

    private void btnAddShapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddShapeActionPerformed
        if(lastPageLayerReference != null){
            ShapeLayer sl = new ShapeLayer();
        
            InputDialog id = new InputDialog(parent, true);
            id.setDialogTitle("Shape name");
            id.setDialogMessage("Please type a name for this shape :");
            sl.setName(id.showDialog(sl.getName()));

            lastPageLayerReference.getShapes().add(sl);
//            listModel.addElement(sl);
            updateTree();
        }
    }//GEN-LAST:event_btnAddShapeActionPerformed

    private void btnAddTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTextActionPerformed
        if(lastPageLayerReference != null){
            TextLayer tl = new TextLayer();
        
            InputDialog id = new InputDialog(parent, true);
            id.setDialogTitle("Text");
            id.setDialogMessage("Please type the text to add onto the image :");
            tl.setText(TextLayer.ISO_3166.United_States_of_America, id.showDialog("My text here."));        

            lastPageLayerReference.getTexts().add(tl);
//            listModel.addElement(tl);
            searchForLanguage();
            page.repaint();
            updateTree();
        }
    }//GEN-LAST:event_btnAddTextActionPerformed

    private void btnAddLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddLineActionPerformed
        page.setSelectedShape(Page.ShapeSelection.Line);
    }//GEN-LAST:event_btnAddLineActionPerformed

    private void btnAddCurveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCurveActionPerformed
        page.setSelectedShape(Page.ShapeSelection.Curve);
    }//GEN-LAST:event_btnAddCurveActionPerformed

    private void cbFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFontActionPerformed
        if(lastSelectedTextLayer != null){
            Font oldFont = lastSelectedTextLayer.getTextFont();
            lastSelectedTextLayer.setTextFont(new Font((String)cbFont.getSelectedItem(), oldFont.getStyle(), oldFont.getSize()));
            page.repaint();
        }
    }//GEN-LAST:event_cbFontActionPerformed

    private void cbFontStyleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbFontStyleActionPerformed
        if(lastSelectedTextLayer != null){
            Font oldFont = lastSelectedTextLayer.getTextFont();
            lastSelectedTextLayer.setTextFont(new Font(oldFont.getFamily(), ((FontStyle)cbFontStyle.getSelectedItem()).getStyle(), oldFont.getSize()));
            page.repaint();
        }
    }//GEN-LAST:event_cbFontStyleActionPerformed

    private void spinFontSizeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinFontSizeStateChanged
        if(lastSelectedTextLayer != null){
            Font oldFont = lastSelectedTextLayer.getTextFont();
            lastSelectedTextLayer.setTextFont(new Font(oldFont.getFamily(), oldFont.getStyle(), (int)spinFontSize.getValue()));
            page.repaint();
        }
    }//GEN-LAST:event_spinFontSizeStateChanged

    private void cbAvailableLanguagesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAvailableLanguagesItemStateChanged
//        for(Object obj : listModel.toArray()){
//            if(obj instanceof TextLayer){
//                TextLayer tl = (TextLayer)obj;
//                tl.setCountry((TextLayer.ISO_3166)cbAvailableLanguages.getSelectedItem());
//                page.repaint();
//            }
//        }
        
        for(int i=0; i<jTree1.getRowCount(); i++){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getPathForRow(i).getLastPathComponent();
            if(node.getUserObject() instanceof TextLayer){
                TextLayer tl = (TextLayer)node.getUserObject();
                tl.setCountry((TextLayer.ISO_3166)cbAvailableLanguages.getSelectedItem());
                page.repaint();
            }
        }
    }//GEN-LAST:event_cbAvailableLanguagesItemStateChanged

    private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
        try{
            //Nettoyage
            for(PageLayer pl : page.getPages()){
                for(ShapeLayer sl : pl.getShapes()){
                    sl.showBuilding(false);
                }
                for(TextLayer tl : pl.getTexts()){
                    tl.showBuilding(false);
                }
            }

            DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getSelectionPath().getLastPathComponent();
            Object obj = node.getUserObject();
            lastSelectedShapeLayer = null;
            lastSelectedTextLayer = null;
            lastSelectedPageLayer = null;
            if(obj instanceof ShapeLayer){
                lastSelectedShapeLayer = (ShapeLayer)obj;
                lastSelectedShapeLayer.showBuilding(true);
            }
            if(obj instanceof TextLayer){
                lastSelectedTextLayer = (TextLayer)obj;
                lastSelectedTextLayer.showBuilding(true);
            }
            if(obj instanceof PageLayer){
                lastSelectedPageLayer = (PageLayer)obj;
                lastPageLayerReference = (PageLayer)obj;
                page.setActualPage((PageLayer)obj);
                //UPDATE 22/02/2015
                updateImage((PageLayer)obj);
            }
            page.repaint();
        }catch(Exception e){
            
        }
    }//GEN-LAST:event_jTree1ValueChanged

    private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
        //Si on double clique
        if(evt.getClickCount()==2){
            if(lastSelectedShapeLayer != null){
                ShapeLayerDialog sld = new ShapeLayerDialog(parent, true);
                sld.setShapeName(lastSelectedShapeLayer.getName());
                sld.setShapeColor(lastSelectedShapeLayer.getGeneralPathColor());
                boolean b = sld.showDialog();
                if(b == true){
                    lastSelectedShapeLayer.setName(sld.getShapeName());
                    lastSelectedShapeLayer.setGeneralPathColor(sld.getShapeColor());
                    page.repaint();
                }                
            }
            if(lastSelectedTextLayer != null){
                TextLayerDialog tld = new TextLayerDialog(parent, true);
                tld.setTexts(lastSelectedTextLayer.getTexts());
                tld.setTextColor(lastSelectedTextLayer.getTextColor());
                tld.setTextFont(lastSelectedTextLayer.getTextFont());
                boolean b = tld.showDialog();
                if(b == true){
                    lastSelectedTextLayer.setTexts(tld.getTexts());
                    lastSelectedTextLayer.setTextColor(tld.getTextColor());
                    lastSelectedTextLayer.setTextFont(tld.getTextFont());
                    searchForLanguage();
                    page.repaint();                    
                }
            }
            if(lastSelectedPageLayer != null){
//                try {
//                    CNMWriter cw = new CNMWriter();
//                    cw.setPageLayer(lastPageLayerReference);
//                    cw.setImageLayer(lastPageLayerReference.getImages().get(0));
//                    cw.setShapeLayerList(lastPageLayerReference.getShapes());
//                    cw.setTextLayerList(lastPageLayerReference.getTexts());
//                    MainUniverse main = new MainUniverse();
//                    main.setFirstName("Thomas");
//                    main.setLastName("Johnson");
//                    main.setMacAddress();
//                    main.getLOL().setReader("free");
//                    main.getLOL().setCopyright(true);
//                    cw.setMainUniverse(main);
//                    cw.write("C:\\Users\\Yves\\Desktop\\testCNM.cnm");
//                } catch (UnsupportedEncodingException ex) {
//                    Logger.getLogger(PageCreator.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (IOException ex) {
//                    Logger.getLogger(PageCreator.class.getName()).log(Level.SEVERE, null, ex);
//                }
                
                
            }
        }
    }//GEN-LAST:event_jTree1MouseClicked

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        for (FileFilter f : fcXML.getChoosableFileFilters()){
            fcXML.removeChoosableFileFilter(f);
        }
        fcXML.setDialogTitle("Save the document...");
        fcXML.setDialogType(JFileChooser.SAVE_DIALOG);
        fcXML.setFileFilter(new CNMFilter());
        int z = fcXML.showSaveDialog(this);
        if (z == JFileChooser.APPROVE_OPTION){
            Writer writer = new Writer();
            DualLock duallock = new DualLock();
            writer.setDualLock(duallock);
            writer.setPages(page);
            
            String path = fcXML.getSelectedFile().getAbsolutePath();
            if(path.endsWith(".cnm")==false){
                path = path + ".cnm";
            }

            writer.createXML(path);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        for (FileFilter f : fcXML.getChoosableFileFilters()){
            fcXML.removeChoosableFileFilter(f);
        }
        fcXML.setDialogTitle("Open the document...");
        fcXML.setDialogType(JFileChooser.OPEN_DIALOG);
        fcXML.setFileFilter(new CNMFilter());
        int z = fcXML.showOpenDialog(this);
        if (z == JFileChooser.APPROVE_OPTION){
            try {
                Reader reader = new Reader(fcXML.getSelectedFile().getAbsolutePath());
                page = reader.getPage();
                page.setActualPage(page.getPages().get(0));
                updateImage(page.getPages().get(0));
                page.repaint();
                updateTree();
                searchForLanguage();
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                
            }            
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDataActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDataActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        int z = JOptionPane.showConfirmDialog(parent, "Do you really want to create a new document ?", "New document...", JOptionPane.YES_NO_OPTION);
        if(z == JOptionPane.YES_OPTION){
            root.removeAllChildren();
            page.getPages().clear();
            page.setActualPage(null);
            editorScrollPane.setVisible(false);
            updateTree();
        }
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUndoActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getSelectionPath().getLastPathComponent();
        Object obj = node.getUserObject();
        if(obj instanceof ShapeLayer){
            ShapeLayer sl = (ShapeLayer)obj;
            sl.undo();
            page.repaint();
        }
        if(obj instanceof TextLayer){
            TextLayer tl = (TextLayer)obj;
            
        }
    }//GEN-LAST:event_btnUndoActionPerformed

    private void btnRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedoActionPerformed
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)jTree1.getSelectionPath().getLastPathComponent();
        Object obj = node.getUserObject();
        if(obj instanceof ShapeLayer){
            ShapeLayer sl = (ShapeLayer)obj;
            sl.redo();
            page.repaint();
        }
        if(obj instanceof TextLayer){
            TextLayer tl = (TextLayer)obj;
            
        }
    }//GEN-LAST:event_btnRedoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCurve;
    private javax.swing.JButton btnAddImage;
    private javax.swing.JButton btnAddLine;
    private javax.swing.JButton btnAddPage;
    private javax.swing.JButton btnAddShape;
    private javax.swing.JButton btnAddText;
    private javax.swing.JButton btnData;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnRedo;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUndo;
    private javax.swing.JComboBox cbAvailableLanguages;
    private javax.swing.JComboBox cbFont;
    private javax.swing.JComboBox cbFontStyle;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel editorPanel;
    private javax.swing.JFileChooser fcImage;
    private javax.swing.JFileChooser fcXML;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTree jTree1;
    private javax.swing.JSpinner spinFontSize;
    // End of variables declaration//GEN-END:variables
}
