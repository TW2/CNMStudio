/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer.vector;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 *
 * @author Yves
 */
public abstract class AbstractShape implements ShapeInterface {
    
    protected Point2D start, end, control_1, control_2, first;
    
    public AbstractShape(){
        
    }

    @Override
    public void setStartPoint(Point2D p) {
        start = p;
    }

    @Override
    public Point2D getStartPoint() {
        return start;
    }

    @Override
    public void setEndPoint(Point2D p) {
        end = p;
    }

    @Override
    public Point2D getEndPoint() {
        return end;
    }

    @Override
    public void setControlPoint_1(Point2D p) {
        control_1 = p;
    }

    @Override
    public Point2D getControlPoint_1() {
        return control_1;
    }

    @Override
    public void setControlPoint_2(Point2D p) {
        control_2 = p;
    }

    @Override
    public Point2D getControlPoint_2() {
        return control_2;
    }

    @Override
    public void setVeryFirstPoint(Point2D p) {
        first = p;
    }

    @Override
    public Point2D getVeryFirstPoint() {
        return first;
    }

    @Override
    public SelectedPoint hasSelection(Point2D p) {
        Rectangle rect = new Rectangle((int)p.getX()-5, (int)p.getY()-5, 10, 10);
        
        if(rect.contains(start)){
            return SelectedPoint.Start;
        }else if(rect.contains(end)){
            return SelectedPoint.End;
        }else if(rect.contains(control_1)){
            return SelectedPoint.Control_1;
        }else if(rect.contains(control_2)){
            return SelectedPoint.Control_2;
        }
        return SelectedPoint.None;
    }

    @Override
    public Point2D getSelectedPoint(Point2D p) {
        SelectedPoint sp = hasSelection(p);
        if(sp==SelectedPoint.Start){
            return start;
        }else if(sp==SelectedPoint.End){
            return end;
        }else if(sp==SelectedPoint.Control_1){
            return control_1;
        }else if(sp==SelectedPoint.Control_2){
            return control_2;
        }else{
            return null;
        }
    }
    
    public boolean hasPoint2D(Point2D p){
        return start.equals(p) | end.equals(p);
    }
    
}
