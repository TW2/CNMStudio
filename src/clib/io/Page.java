/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clib.io;

import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.layer.vector.SharedPoint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author Yves
 */
public class Page extends JPanel {
    
    List<PageLayer> pages = new ArrayList<>();
//    List<ImageLayer> images = new ArrayList<>();
//    List<ShapeLayer> shapes = new ArrayList<>();
//    List<TextLayer> texts = new ArrayList<>();
    PageLayer actualPage = null;
    private ShapeSelection shapeSelected = ShapeSelection.None;
    private SharedPoint lastSharedPoint = null;
    private ImageLayer lastImageLayer = null;
    private Mode mode = Mode.Free;
    
    public enum ShapeSelection{
        None, Line, Curve, Move, Point, ControlPoint;
    }
    
    public enum Mode{
        Free, Encrypted;
    }
    
    public Page(){
        setSize(400,800);
    }
    
    public List<PageLayer> getPages(){
        return pages;
    }
    
    public List<ImageLayer> getImagesFrom(PageLayer pagelayer){
        return pagelayer.getImages();
    }
    
    public List<ShapeLayer> getShapesFrom(PageLayer pagelayer){
        return pagelayer.getShapes();
    }
    
    public List<TextLayer> getTextsFrom(PageLayer pagelayer){
        return pagelayer.getTexts();
    }
    
    public void draw(Graphics2D g2){
        if(actualPage != null){
            if(getImagesFrom(actualPage).isEmpty() == false){
                for(ImageLayer img : getImagesFrom(actualPage)){
                    img.drawImage(g2); lastImageLayer = img;
                }

                for(ShapeLayer sh : getShapesFrom(actualPage)){
                    sh.draw(g2, 0f, 0f);
                }

                for(TextLayer te : getTextsFrom(actualPage)){
                    te.draw(g2);
                }
            }else if(lastImageLayer != null){
                lastImageLayer.drawImage(g2);
            }
        }
    }
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        draw(g2);
    }
    
    public void setSelectedShape(ShapeSelection s){
        shapeSelected = s;
    }
    
    public void setActualPage(PageLayer pl){
        actualPage = pl;
    }
    
     public void setMode(Mode mode){
        this.mode = mode;
    }
    
    public Mode getMode(){
        return mode;
    }
    
}
