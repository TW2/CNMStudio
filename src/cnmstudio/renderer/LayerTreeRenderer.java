/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cnmstudio.renderer;

import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Yves
 */
public class LayerTreeRenderer extends JPanel implements TreeCellRenderer {
    
    JLabel lblName = new JLabel("Object");
    JLabel lblDescription = new JLabel("Object description");
    JPanel pMain = new JPanel(new BorderLayout());
    JLabel lblType = new JLabel(" ");

    public LayerTreeRenderer(){
        setOpaque(true);
        setLayout(new BorderLayout());
        add(lblType, BorderLayout.WEST);
        add(pMain, BorderLayout.CENTER);
        pMain.setBackground(Color.white);
        pMain.add(lblName, BorderLayout.NORTH);
        pMain.add(lblDescription, BorderLayout.SOUTH);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        setBackground(selected ? SystemColor.textHighlight : Color.white);
        setForeground(selected ? Color.white : Color.black);
        pMain.setBackground(selected ? SystemColor.textHighlight : Color.white);
        pMain.setForeground(selected ? Color.white : Color.black);
        
        if(value instanceof DefaultMutableTreeNode){
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            
            if(node.getUserObject() instanceof PageLayer){
                PageLayer pl = (PageLayer)node.getUserObject();

                lblName.setText("  Page  ");
                lblName.setFont(lblName.getFont().deriveFont(15f));

                lblDescription.setText("   " + pl.getName() + "   ");
                lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
                lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
                lblDescription.setForeground(new Color(192,192,192));
                
                lblType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/small_document.png")));

            }else if(node.getUserObject() instanceof ImageLayer){
                ImageLayer il = (ImageLayer)node.getUserObject();

                lblName.setText("  Image  ");
                lblName.setFont(lblName.getFont().deriveFont(15f));

                lblDescription.setText("   " + il.getName() + "   ");
                lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
                lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
                lblDescription.setForeground(new Color(192,192,192));
                
                lblType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/small_image.png")));

            }else if(node.getUserObject() instanceof ShapeLayer){
                ShapeLayer sl = (ShapeLayer)node.getUserObject();

                lblName.setText("  Shape  ");
                lblName.setFont(lblName.getFont().deriveFont(15f));

                lblDescription.setText("   " + sl.getName() + "   ");
                lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
                lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
                lblDescription.setForeground(new Color(192,192,192));
                
                lblType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/small_shape.png")));

            }else if(node.getUserObject() instanceof TextLayer){
                TextLayer tl = (TextLayer)node.getUserObject();

                lblName.setText("  Text  ");
                lblName.setFont(lblName.getFont().deriveFont(15f));

                lblDescription.setText("   " + tl.getText(tl.getCountry()) + "   ");
                lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
                lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
                lblDescription.setForeground(new Color(192,192,192));
                
                lblType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/small_text.png")));

            }else{
                String s = (String)node.getUserObject();
                lblName.setText("  "+s+"  ");
                lblName.setFont(lblName.getFont().deriveFont(12f));            
                lblDescription.setText("");
                
                lblType.setIcon(new javax.swing.ImageIcon(getClass().getResource("/clib/images/small_pages.png")));
            }
        }

        return this;
    }
}
