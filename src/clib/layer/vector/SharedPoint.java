/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Yves
 */
public class SharedPoint extends AbstractShape {
    
    private boolean isControlPoint = false;
    
    public SharedPoint(){
        
    }
    
    public SharedPoint(double x1, double y1){
        start = new Point2D.Double(x1, y1);
        end = new Point2D.Double(x1, y1);
    }
    
    public SharedPoint(Point2D start){
        this.start = start;
        end = start;
    }

    /**
     *
     * @param g
     * @param c
     */
    @Override
    public void draw(Graphics2D g, Color c) {
        g.setColor(c);
        if(isControlPoint==true){
            g.fill(new Ellipse2D.Double(start.getX()-5, start.getY()-5, 10, 10));
        }else{
            g.fill(new Rectangle2D.Double(start.getX()-5, start.getY()-5, 10, 10));
        }        
    }
    
    public void setPointType(boolean controlPoint){
        isControlPoint = controlPoint;
    }
    
    public boolean isControlPoint(){
        return isControlPoint;
    }
    
}
