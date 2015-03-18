/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Yves
 */
public interface ShapeInterface {
    
    public enum SelectedPoint{
        None, Start, End, Control_1, Control_2;
    }
    
    public void draw(Graphics2D g, Color c);
    
    public void setStartPoint(Point2D p);
    
    public Point2D getStartPoint();
    
    public void setEndPoint(Point2D p);
    
    public Point2D getEndPoint();
    
    public void setControlPoint_1(Point2D p);
    
    public Point2D getControlPoint_1();
    
    public void setControlPoint_2(Point2D p);
    
    public Point2D getControlPoint_2();
    
    public void setVeryFirstPoint(Point2D p);
    
    public Point2D getVeryFirstPoint();
    
    public SelectedPoint hasSelection(Point2D p);
    
    public Point2D getSelectedPoint(Point2D p);
    
}
