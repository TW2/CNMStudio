/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Yves
 */
public class Line extends AbstractShape {
    
    public Line(){
        
    }
    
    public Line(double x1, double y1, double x2, double y2){
        start = new Point2D.Double(x1, y1);
        end = new Point2D.Double(x2, y2);
    }
    
    public Line(Point2D start, Point2D end){
        this.start = start;
        this.end = end;
    }

    /**
     *
     * @param g
     * @param c
     */
    @Override
    public void draw(Graphics2D g, Color c) {
        g.setColor(c);
        g.draw(new Line2D.Double(start, end));
    }
}
