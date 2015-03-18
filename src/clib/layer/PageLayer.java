/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Yves
 */
public class PageLayer {
    
    private String name = "Page name";
    private int width = 0, height = 0;
    List<ImageLayer> images = new ArrayList<>();
    List<ShapeLayer> shapes = new ArrayList<>();
    List<TextLayer> texts = new ArrayList<>();
    
    public PageLayer(){
        
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setWidth(int width){
        this.width = width;
    }
    
    public int getWidth(){
        return width;
    }
    
    public void setHeight(int height){
        this.height = height;
    }
    
    public int getHeight(){
        return height;
    }
    
    public List<ImageLayer> getImages(){
        return images;
    }
    
    public List<ShapeLayer> getShapes(){
        return shapes;
    }
    
    public List<TextLayer> getTexts(){
        return texts;
    }
    
}
