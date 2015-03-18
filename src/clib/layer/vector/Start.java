/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clib.layer.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Yves
 * Add 23/02/2015
 */
public class Start extends AbstractShape {

    public Start(){
        
    }
    
    public Start(double x1, double y1){
        start = new Point2D.Double(x1, y1);
        end = new Point2D.Double(x1, y1);
    }
    
    public Start(Point2D start){
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
        g.fill(new Rectangle2D.Double(start.getX()-5, start.getY()-5, 10, 10));
    }
    
}
