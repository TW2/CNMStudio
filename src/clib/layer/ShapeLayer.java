/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer;

import clib.layer.vector.AbstractShape;
import clib.layer.vector.Curve;
import clib.layer.vector.Line;
import clib.layer.vector.Move;
import clib.layer.vector.SharedPoint;
import clib.layer.vector.Start;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Yves
 */
public class ShapeLayer {
    
    private final List<AbstractShape> shapes = new ArrayList<>();
    private Color color = Color.yellow;
    private String name = "ID";
    private boolean showBuilding = false;
    
    private final List<AbstractShape> historylist = new ArrayList<>(); //Ajout ShapeLayer v2
    
    public ShapeLayer(){
        
    }
    
    public void addShape(AbstractShape as){
        shapes.add(as);
        cloneList(); //Ajout ShapeLayer v2
    }
    
    public void removeShape(AbstractShape as){
        shapes.remove(as);
        cloneList(); //Ajout ShapeLayer v2
    }
    
    public void clearShapes(){
        shapes.clear();
        cloneList(); //Ajout ShapeLayer v2
    }
    
    public List<AbstractShape> getShapes(){
        return shapes;
    }
    
    public Line getLastLine(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof Line){
                return (Line)as;
            }
        }
        return null;
    }
    
    public Curve getLastCurve(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof Curve){
                return (Curve)as;
            }
        }
        return null;
    }
    
    public SharedPoint getLastPoint(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof SharedPoint){
                SharedPoint sp = (SharedPoint)as;
                if(sp.isControlPoint()==false){
                    return sp;
                }               
            }
        }
        return null;
    }
    
    public SharedPoint getLastControlPoint(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof SharedPoint){
                SharedPoint sp = (SharedPoint)as;
                if(sp.isControlPoint()==true){
                    return sp;
                }               
            }
        }
        return null;
    }
    
    public Move getLastMove(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof Move){
                return (Move)as;
            }
        }
        return null;
    }
    
    public Start getLastStart(){
        for(int i=shapes.size()-1; i>=0; i--){
            AbstractShape as = shapes.get(i);
            if(as instanceof Start){
                return (Start)as;
            }
        }
        return null;
    }
    
    public GeneralPath getGeneralPath(){
        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        int count = 0;
        for(AbstractShape s : shapes){
            // Add to the path
            if(s instanceof Line){
                Line l = (Line)s;
                if(count==0){
                    gp.moveTo(l.getStartPoint().getX(), l.getStartPoint().getY());
                }else{
                    gp.lineTo(l.getEndPoint().getX(), l.getEndPoint().getY());
                }
            }else if(s instanceof Curve){
                Curve b = (Curve)s;
                if(count==0){
                    gp.moveTo(b.getStartPoint().getX(), b.getStartPoint().getY());
                }else{
                    gp.curveTo(b.getControlPoint_1().getX(), b.getControlPoint_1().getY(),
                            b.getControlPoint_2().getX(), b.getControlPoint_2().getY(),
                            b.getEndPoint().getX(), b.getEndPoint().getY());
                }
            }else if(s instanceof SharedPoint){
                if(count==0){
                    SharedPoint p = (SharedPoint)s;
                    gp.moveTo(p.getStartPoint().getX(), p.getStartPoint().getY());
                }
            }else if(s instanceof Move){
                Move m = (Move)s;
                try{
                    gp.lineTo(m.getEndPoint().getX(), m.getEndPoint().getY());
                }catch(Exception e){
                    gp.moveTo(m.getEndPoint().getX(), m.getEndPoint().getY());
                }                
            }else if(s instanceof Start){
                Start p = (Start)s;
                gp.moveTo(p.getStartPoint().getX(), p.getStartPoint().getY());
            }
            count+=1;
        }
        return gp;
    }
    
    public void drawLines(Graphics2D g, float x, float y){
        AffineTransform at = g.getTransform();
//        AffineTransform mod = new AffineTransform();
//        mod.setToTranslation(x, y);
//        g.setTransform(mod);
        for(AbstractShape as : shapes){
            if(as instanceof SharedPoint){
                SharedPoint sp = (SharedPoint)as;
                if(sp.isControlPoint()==false){
                    sp.draw(g, Color.blue);
                }else{
                    sp.draw(g, Color.orange);
                }
            }else if(as instanceof Line){
                Line l = (Line)as;
                l.draw(g, Color.red);
            }else if(as instanceof Curve){
                Curve c = (Curve)as;                
                c.draw(g, Color.magenta);
                c.drawRaw(g, Color.red);
            }else if(as instanceof Start){
                Start s = (Start)as;
                s.draw(g, Color.cyan);
            }
        }
        g.setTransform(at);
    }
    
    public void drawGeneralPath(Graphics2D g){
        g.setColor(color);
        g.fill(getGeneralPath());
    }
    
    public void draw(Graphics2D g, float x, float y){
        drawGeneralPath(g);
        if(showBuilding == true){
            drawLines(g, x, y);
        }
    }
    
    public SharedPoint getSharedPointAt(int x, int y){
        Rectangle rect = new Rectangle(x-10, y-10, 20, 20);
        for(AbstractShape as : shapes){
            if(as instanceof SharedPoint){
                SharedPoint sp = (SharedPoint)as;
                if(rect.contains(sp.getStartPoint())){
                    return sp;
                }
            }
        }
        return null;
    }
    
    public Start getStartPointAt(int x, int y){
        Rectangle rect = new Rectangle(x-10, y-10, 20, 20);
        for(AbstractShape as : shapes){
            if(as instanceof Start){
                Start s = (Start)as;
                if(rect.contains(s.getStartPoint())){
                    return s;
                }
            }
        }
        return null;
    }
    
    public List<AbstractShape> getClosestShapes(SharedPoint sp){
        List<AbstractShape> rshapes = new ArrayList<>();
        for(AbstractShape as : shapes){
            if(sp.isControlPoint()==true && as instanceof Curve){
                Curve curve = (Curve)as;
                if(curve.getControl1().equals(sp) | curve.getControl2().equals(sp)){
                    rshapes.add(as);
                    rshapes.add(curve.getControl1());
                    rshapes.add(curve.getControl2());
                }
            }else if(as.hasPoint2D(sp.getStartPoint())){
                rshapes.add(as);
            }
        }
        return rshapes;
    }
    
    public List<AbstractShape> getClosestShapes(Start start){
        List<AbstractShape> rshapes = new ArrayList<>();
        for(AbstractShape as : shapes){
            if(as instanceof Curve){
                Curve curve = (Curve)as;
                if(curve.getStartPoint().equals(start.getStartPoint())){
                    rshapes.add(as);
                    rshapes.add(curve.getControl1());
                    rshapes.add(curve.getControl2());
                }
            }else if(as instanceof Line){
                Line line = (Line)as;
                if(line.getStartPoint().equals(start.getStartPoint())){
                    rshapes.add(as);
                }
            }
        }
        return rshapes;
    }
    
    public void updatePoint2D(AbstractShape shape, Point2D oldp, Point2D newp){
        if(shape.getStartPoint().equals(oldp)){
            shape.setStartPoint(newp);
        }else if(shape.getEndPoint().equals(oldp)){
            shape.setEndPoint(newp);
        }else if(shape.getControlPoint_1().equals(oldp)){
            shape.setControlPoint_1(newp);
        }else if(shape.getControlPoint_2().equals(oldp)){
            shape.setControlPoint_2(newp);
        }
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setGeneralPathColor(Color color){
        this.color = color;
    }
    
    public Color getGeneralPathColor(){
        return color;
    }
    
    public void showBuilding(boolean showBuilding){
        this.showBuilding = showBuilding;
    }
    
    //==========================================================================
    //========== EXPORT
    //==========================================================================
    
    public String getStringCommands(){
        String value = "";
        for(AbstractShape as : getShapes()){                
            if(as instanceof Move){
                Move move = (Move)as;
                value = value + 
                        "M"+move.getStartPoint().getX()+","+move.getStartPoint().getY()+";";
            }else if(as instanceof Line){
                Line line = (Line)as;
                value = value + 
                        "L"+line.getEndPoint().getX()+","+line.getEndPoint().getY()+";";
            }else if(as instanceof Curve){
                Curve curve = (Curve)as;
                value = value + 
                        "C"+curve.getControlPoint_1().getX()+","+curve.getControlPoint_1().getY()+","
                        +curve.getControlPoint_2().getX()+","+curve.getControlPoint_2().getY()+","
                        +curve.getEndPoint().getX()+","+curve.getEndPoint().getY()+";";
            }else if(as instanceof Start){
                Start start = (Start)as;
                value = value + 
                        "S"+start.getEndPoint().getX()+","+start.getEndPoint().getY()+";";
            }
        }
        return value;
    }
    
    public String getStringColor(){
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String red = Integer.toHexString(r).length()<2 ? "0"+Integer.toHexString(r) : Integer.toHexString(r);
        String green = Integer.toHexString(g).length()<2 ? "0"+Integer.toHexString(g) : Integer.toHexString(g);
        String blue = Integer.toHexString(b).length()<2 ? "0"+Integer.toHexString(b) : Integer.toHexString(b);
        return red+green+blue;
    }
    
    //==========================================================================
    //========== IMPORT - 23/02/2015
    //==========================================================================
    
    public List<AbstractShape> getShapesFromString(String s){
        
        List<AbstractShape> list = new ArrayList<>();
        //Format :
        //Mx,y
        //Lx,y
        //Ccpx1,cpy1,cpx2,cpy2,x,y
        
        double lastX = 0d, lastY = 0d;
        
        String[] table = s.split(";");
        
        for(String t : table){
            if(t.startsWith("M")){
                Pattern p = Pattern.compile("M([^,]+),(.+)");
                Matcher m = p.matcher(t);
                
                if(m.matches()){
                    Move move = new Move(
                            lastX,
                            lastY, 
                            Double.parseDouble(m.group(1)), 
                            Double.parseDouble(m.group(2)));

                    lastX = Double.parseDouble(m.group(1));
                    lastY = Double.parseDouble(m.group(2));

                    list.add(move);
                }                
            }else if(t.startsWith("L")){
                Pattern p = Pattern.compile("L([^,]+),(.+)");
                Matcher m = p.matcher(t);
                
                if(m.matches()){
                     Line line = new Line(
                            lastX,
                            lastY, 
                            Double.parseDouble(m.group(1)), 
                            Double.parseDouble(m.group(2)));

                    lastX = Double.parseDouble(m.group(1));
                    lastY = Double.parseDouble(m.group(2));

                    list.add(line);
                }               
            }else if(t.startsWith("C")){
                Pattern p = Pattern.compile("C([^,]+),([^,]+),([^,]+),([^,]+),([^,]+),(.+)");
                Matcher m = p.matcher(t);
                
                if(m.matches()){
                    Curve curve = new Curve(
                            lastX,
                            lastY,
                            Double.parseDouble(m.group(1)),
                            Double.parseDouble(m.group(2)),
                            Double.parseDouble(m.group(3)),
                            Double.parseDouble(m.group(4)),
                            Double.parseDouble(m.group(5)),
                            Double.parseDouble(m.group(6)));

                    lastX = Double.parseDouble(m.group(5));
                    lastY = Double.parseDouble(m.group(6));

                    list.add(curve);
                }                
            }else if(t.startsWith("S")){
                Pattern p = Pattern.compile("S([^,]+),(.+)");
                Matcher m = p.matcher(t);
                
                if(m.matches()){
                    Start start = new Start(
                            Double.parseDouble(m.group(1)), 
                            Double.parseDouble(m.group(2)));

                    lastX = Double.parseDouble(m.group(1));
                    lastY = Double.parseDouble(m.group(2));

                    list.add(start);
                }                
            }
        }
        
        return list;
    }
    
    public Color getColorFromString(String s){
        String red = s.substring(0, 2);
        String green = s.substring(2, 4);
        String blue = s.substring(4);
        int r = Integer.parseInt(red, 16);
        int g = Integer.parseInt(green, 16);
        int b = Integer.parseInt(blue, 16);
        return new Color(r, g, b);
    }
    
    //==========================================================================
    //========== Ajout ShapeLayer v2 : Système Undo Redo - 21/02/2015
    //==========================================================================
    
    public void cloneList(){
        historylist.clear();
        for(AbstractShape as : shapes){
            historylist.add(as);
        }
    }
    
    public void undo(){
        if(shapes.size() > 0){
            Curve c = getLastCurve();
            Line l = getLastLine();
            
            if(c!=null && l!=null){
                if(shapes.indexOf(c) > shapes.indexOf(l)){
                    //On a une courbe à supprimer (point controlpoint controlpoint curve)
                    shapes.remove(shapes.size()-1); //point
                    shapes.remove(shapes.size()-1); //controlpoint
                    shapes.remove(shapes.size()-1); //controlpoint
                    shapes.remove(shapes.size()-1); //curve
                }else{
                    //On a une ligne à supprimer (point line)
                    shapes.remove(shapes.size()-1); //point
                    shapes.remove(shapes.size()-1); //line
                }
            }else if(c!=null){
                //On a une courbe à supprimer (point controlpoint controlpoint curve)
                shapes.remove(shapes.size()-1); //point
                shapes.remove(shapes.size()-1); //controlpoint
                shapes.remove(shapes.size()-1); //controlpoint
                shapes.remove(shapes.size()-1); //curve
            }else if(l!=null){
                //On a une ligne à supprimer (point line)
                shapes.remove(shapes.size()-1); //point
                shapes.remove(shapes.size()-1); //line
            }else if(shapes.size() == 1){
                // On a un début à supprimer (start)
                shapes.remove(shapes.size()-1); //start
            }
        }        
    }
    
    public void redo(){
        if(shapes.size() < historylist.size()){
            if(historylist.get(shapes.size()) instanceof Curve){
                //On a une courbe à ajouter (point controlpoint controlpoint curve)
                shapes.add(historylist.get(shapes.size())); //curve
                shapes.add(historylist.get(shapes.size())); //controlpoint
                shapes.add(historylist.get(shapes.size())); //controlpoint
                shapes.add(historylist.get(shapes.size())); //point
            }else if(historylist.get(shapes.size()) instanceof Line){
                //On a une ligne à ajouter (point line)
                shapes.add(historylist.get(shapes.size())); //line
                shapes.add(historylist.get(shapes.size())); //point
            }else if(historylist.get(shapes.size()) instanceof Start){
                //On a un début à ajouter (start)
                shapes.add(historylist.get(shapes.size())); //start
            }
            
        }
    }
}
