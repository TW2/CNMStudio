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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Yves
 */
public class LayerListRenderer extends JPanel implements ListCellRenderer {
    
    JLabel lblName = new JLabel("Object");
    JLabel lblDescription = new JLabel("Object description");

    public LayerListRenderer(){
        setOpaque(true);
        setLayout(new BorderLayout());
        add(lblName, BorderLayout.NORTH);
        add(lblDescription, BorderLayout.SOUTH);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        if(value instanceof PageLayer){
            PageLayer pl = (PageLayer)value;
            
            setBackground(isSelected ? SystemColor.textHighlight : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            
            lblName.setText("  Page");
            lblName.setFont(lblName.getFont().deriveFont(15f));
            
            lblDescription.setText("   " + pl.getName());
            lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
            lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
            lblDescription.setForeground(new Color(192,192,192));
            
        }else if(value instanceof ImageLayer){
            ImageLayer il = (ImageLayer)value;
            
            setBackground(isSelected ? SystemColor.textHighlight : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            
            lblName.setText("  Image");
            lblName.setFont(lblName.getFont().deriveFont(15f));
            
            lblDescription.setText("   " + il.getName());
            lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
            lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
            lblDescription.setForeground(new Color(192,192,192));
            
        }else if(value instanceof ShapeLayer){
            ShapeLayer sl = (ShapeLayer)value;
            
            setBackground(isSelected ? SystemColor.textHighlight : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            
            lblName.setText("  Shape");
            lblName.setFont(lblName.getFont().deriveFont(15f));
            
            lblDescription.setText("   " + sl.getName());
            lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
            lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
            lblDescription.setForeground(new Color(192,192,192));
            
        }else if(value instanceof TextLayer){
            TextLayer tl = (TextLayer)value;
            
            setBackground(isSelected ? SystemColor.textHighlight : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            
            lblName.setText("  Text");
            lblName.setFont(lblName.getFont().deriveFont(15f));
            
            lblDescription.setText("   " + tl.getText(tl.getCountry()));
            lblDescription.setFont(lblDescription.getFont().deriveFont(Font.BOLD));
            lblDescription.setFont(lblDescription.getFont().deriveFont(10f));
            lblDescription.setForeground(new Color(192,192,192));
            
        }

        return this;
    }
}
