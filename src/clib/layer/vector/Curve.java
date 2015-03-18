/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer.vector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Yves
 */
public class Curve extends AbstractShape {
    
    SharedPoint control1, control2;
    
    public Curve(){
        
    }
    
    public Curve(double x1, double y1, double x2, double y2){
        start = new Point2D.Double(x1, y1);
        end = new Point2D.Double(x2, y2);
        
        double xdiff = end.getX() - start.getX();
        double ydiff = end.getY() - start.getY();

        double x1_3 = start.getX() + xdiff/3;
        double x2_3 = start.getX() + xdiff*2/3;
        double y1_3 = start.getY() + ydiff/3;
        double y2_3 = start.getY() + ydiff*2/3;
        
        control1 = new SharedPoint(x1_3, y1_3);
        control1.setPointType(true);
        control2 = new SharedPoint(x2_3, y2_3);
        control2.setPointType(true);
        
        control_1 = new Point2D.Double(x1_3, y1_3);
        control_2 = new Point2D.Double(x2_3, y2_3);
    }
    
    public Curve(Point2D start, Point2D end){
        this.start = start;
        this.end = end;
        
        double xdiff = end.getX() - start.getX();
        double ydiff = end.getY() - start.getY();

        double x1_3 = start.getX() + xdiff/3;
        double x2_3 = start.getX() + xdiff*2/3;
        double y1_3 = start.getY() + ydiff/3;
        double y2_3 = start.getY() + ydiff*2/3;
        
        control1 = new SharedPoint(x1_3, y1_3);
        control1.setPointType(true);
        control2 = new SharedPoint(x2_3, y2_3);
        control2.setPointType(true);
        
        control_1 = new Point2D.Double(x1_3, y1_3);
        control_2 = new Point2D.Double(x2_3, y2_3);
    }
    
    //UPDATE - 23/02/2015
    public Curve(double x1, double y1, double cpx1, double cpy1, double cpx2, double cpy2, double x2, double y2){
        start = new Point2D.Double(x1, y1);
        end = new Point2D.Double(x2, y2);
        
        control1 = new SharedPoint(cpx1, cpy1);
        control1.setPointType(true);
        control2 = new SharedPoint(cpx2, cpy2);
        control2.setPointType(true);
        
        control_1 = new Point2D.Double(cpx1, cpy1);
        control_2 = new Point2D.Double(cpx2, cpy2);
    }

    /**
     *
     * @param g
     * @param c
     */
    @Override
    public void draw(Graphics2D g, Color c) {
        g.setColor(c);
        g.draw(new CubicCurve2D.Double(
                start.getX(), 
                start.getY(), 
                control_1.getX(), 
                control_1.getY(), 
                control_2.getX(), 
                control_2.getY(), 
                end.getX(), 
                end.getY()));
    }
    
    public void drawRaw(Graphics2D g, Color c){
        Stroke stroke = g.getStroke();
        g.setColor(c);
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5, new float[]{5f,5f}, 0f));
        g.drawLine((int)start.getX(), (int)start.getY(), (int)control_1.getX(), (int)control_1.getY());
        g.drawLine((int)control_1.getX(), (int)control_1.getY(), (int)control_2.getX(), (int)control_2.getY());
        g.drawLine((int)control_2.getX(), (int)control_2.getY(), (int)end.getX(), (int)end.getY());
        g.setStroke(stroke);
    }
    
    public SharedPoint getControl1(){
        return control1;
    }
    
    public SharedPoint getControl2(){
        return control2;
    }
}
