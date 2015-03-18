/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Yves
 */
public class ImageLayer {
    
    private int width = 0, height = 0;
    private int x = 0, y = 0;
    private BufferedImage image = null;
    private String name = "ID";
    
    public ImageLayer(){
        
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
    
    public void setXOffset(int x){
        this.x = x;
    }
    
    public int getXOffset(){
        return x;
    }
    
    public void setYOffset(int y){
        this.y = y;
    }
    
    public int getYOffset(){
        return y;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setImage(BufferedImage image){
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    public BufferedImage getImage(){
        return image;
    }
    
    public void drawImage(Graphics2D g2){
        g2.drawImage(image, null, x, y);
    }
    
}
