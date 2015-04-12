/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnmstudio;

import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.layer.vector.AbstractShape;
import clib.layer.vector.Curve;
import clib.layer.vector.Line;
import clib.layer.vector.SharedPoint;
import clib.layer.vector.Start;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
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
    private Start lastStartPoint = null;
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
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vtdtMouseClicked(evt);
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                vtdMousePressed(evt);
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                vtdMouseReleased(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                vtdMouseDragged(evt);
            }
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                vtdMouseMoved(evt);
            }
        });
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
    
    // <editor-fold defaultstate="collapsed" desc="-----<EVENTS>-----">
    public void vtdtMouseClicked(java.awt.event.MouseEvent evt){
        double xa = evt.getXOnScreen()-getLocationOnScreen().getX();
        double ya = evt.getYOnScreen()-getLocationOnScreen().getY();
        
        if(PageCreator.getLastShapeLayer() != null){
            if(shapeSelected==ShapeSelection.None){

            }else if(shapeSelected==ShapeSelection.Line){
                if(evt.getButton()==1){
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    if(sl.getShapes().isEmpty()){
                        sl.addShape(new Start(xa, ya));
                    }else{
                        //UPDATE - Ajout Start
                        double x = sl.getLastPoint() != null ? sl.getLastPoint().getStartPoint().getX() : sl.getLastStart().getStartPoint().getX();
                        double y = sl.getLastPoint() != null ? sl.getLastPoint().getStartPoint().getY() : sl.getLastStart().getStartPoint().getY();
                        
                        sl.addShape(new Line(
                                //UPDATE - Ajout ShapeLayer v2 : Système Undo Redo :
                                //Le end point n'est pas mis à jour pour les points
                                x,//.getEndPoint().getX(),
                                y,//.getEndPoint().getY(),
                                xa,
                                ya));
                        sl.addShape(new SharedPoint(xa, ya));
                    }
                }
            }else if(shapeSelected==ShapeSelection.Curve){
                if(evt.getButton()==1){
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    if(sl.getShapes().isEmpty()){
                        sl.addShape(new Start(xa, ya));
                    }else{
                        //UPDATE - Ajout Start
                        double x = sl.getLastPoint() != null ? sl.getLastPoint().getStartPoint().getX() : sl.getLastStart().getStartPoint().getX();
                        double y = sl.getLastPoint() != null ? sl.getLastPoint().getStartPoint().getY() : sl.getLastStart().getStartPoint().getY();
                        
                        Curve c = new Curve(
                                //UPDATE - Ajout ShapeLayer v2 : Système Undo Redo :
                                //Le end point n'est pas mis à jour pour les points
                                x,//.getEndPoint().getX(),
                                y,//.getEndPoint().getY(),
                                xa,
                                ya);
                        sl.addShape(c);
                        sl.addShape(c.getControl1());
                        sl.addShape(c.getControl2());
                        sl.addShape(new SharedPoint(xa, ya));
                    }
                }
            }
            
            repaint();
        }
        
        if(PageCreator.getLastTextLayer() != null){
            if(evt.getButton()==1){
                TextLayer tl = PageCreator.getLastTextLayer();                    
                int xSpace = tl.getXMax()-tl.getXOffset();
                int ySpace = tl.getYMax()-tl.getYOffset();
                tl.setXOffset((int)xa);
                tl.setYOffset((int)ya);
                tl.setXMax((int)xa+xSpace);
                tl.setYMax((int)ya+ySpace);
            }else if(evt.getButton()==3){
                TextLayer tl = PageCreator.getLastTextLayer();
                tl.setXMax((int)xa);
                tl.setYMax((int)ya);
            }
            repaint();
        }
        
        if(evt.getButton() == 1 && MainFrame.isStudio()==false){
            for(TextLayer te : getTextsFrom(actualPage)){
                if(te.existOnCoordinates(evt.getPoint())){
                    te.playAudio();
                }
            }
        }else if(evt.getButton() == 3 && MainFrame.isStudio()==false){
            for(TextLayer te : getTextsFrom(actualPage)){
                if(te.existOnCoordinates(evt.getPoint())){
                    te.stopAudio();
                }
            }
        }
        
        
    }
    
    public void vtdMousePressed(java.awt.event.MouseEvent evt){
        double xa = evt.getXOnScreen()-getLocationOnScreen().getX();
        double ya = evt.getYOnScreen()-getLocationOnScreen().getY();
        
        if(PageCreator.getLastShapeLayer() != null){
            if(shapeSelected==ShapeSelection.None){

            }else if(shapeSelected==ShapeSelection.Line){
                if(evt.getButton()==2){
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    lastSharedPoint = sl.getSharedPointAt(evt.getX(), evt.getY());
                    lastStartPoint = sl.getStartPointAt(evt.getX(), evt.getY());
                }
            }else if(shapeSelected==ShapeSelection.Curve){
                if(evt.getButton()==2){
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    lastSharedPoint = sl.getSharedPointAt(evt.getX(), evt.getY());
                    lastStartPoint = sl.getStartPointAt(evt.getX(), evt.getY());
                }
            }
        }        
        
    }
    
    public void vtdMouseReleased(java.awt.event.MouseEvent evt){
        double xa = evt.getXOnScreen()-getLocationOnScreen().getX();
        double ya = evt.getYOnScreen()-getLocationOnScreen().getY();
        
        if(PageCreator.getLastShapeLayer() != null){
            if(shapeSelected==ShapeSelection.None){

            }else if(shapeSelected==ShapeSelection.Line){
                if(evt.getButton()==2 && lastSharedPoint!=null){
                    lastSharedPoint.setStartPoint(new Point2D.Float(evt.getX(), evt.getY()));
                    ShapeLayer sl = PageCreator.getLastShapeLayer();                    
                    sl.cloneList();
                }else if(evt.getButton()==2 && lastStartPoint!=null){
                    lastStartPoint.setStartPoint(new Point2D.Float(evt.getX(), evt.getY()));
                    ShapeLayer sl = PageCreator.getLastShapeLayer();                    
                    sl.cloneList();
                }
            }else if(shapeSelected==ShapeSelection.Curve){
                if(evt.getButton()==2 && lastSharedPoint!=null){
                    lastSharedPoint.setStartPoint(new Point2D.Float(evt.getX(), evt.getY()));
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    sl.cloneList();
                }else if(evt.getButton()==2 && lastStartPoint!=null){
                    lastStartPoint.setStartPoint(new Point2D.Float(evt.getX(), evt.getY()));
                    ShapeLayer sl = PageCreator.getLastShapeLayer();                    
                    sl.cloneList();
                }
            }
        }
        
    }
    
    public void vtdMouseDragged(java.awt.event.MouseEvent evt){
        double xa = evt.getXOnScreen()-getLocationOnScreen().getX();
        double ya = evt.getYOnScreen()-getLocationOnScreen().getY();
        
        if(PageCreator.getLastShapeLayer() != null){
            if(shapeSelected==ShapeSelection.None){

            }else if(shapeSelected==ShapeSelection.Line){
                if(lastSharedPoint!=null){
                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    List<AbstractShape> ashapes = sl.getClosestShapes(lastSharedPoint);
                    for(AbstractShape as : ashapes){
                        if(as instanceof Line | as instanceof Curve){
                            sl.updatePoint2D(as, lastSharedPoint.getStartPoint(), new_param);
                        }
                    }
                    lastSharedPoint.setStartPoint(new_param);                    
                }else if(lastStartPoint!=null){
                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    List<AbstractShape> ashapes = sl.getClosestShapes(lastStartPoint);
                    for(AbstractShape as : ashapes){
                        if(as instanceof Line | as instanceof Curve){
                            sl.updatePoint2D(as, lastStartPoint.getStartPoint(), new_param);
                        }
                    }
                    lastStartPoint.setStartPoint(new_param);                    
                }
            }else if(shapeSelected==ShapeSelection.Curve){
                if(lastSharedPoint!=null){
                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    List<AbstractShape> ashapes = sl.getClosestShapes(lastSharedPoint);
                    for(AbstractShape as : ashapes){
                        if(as instanceof Line | as instanceof Curve){
                            sl.updatePoint2D(as, lastSharedPoint.getStartPoint(), new_param);
                        } 
                    }
                    lastSharedPoint.setStartPoint(new_param);
                }else if(lastStartPoint!=null){
                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
                    ShapeLayer sl = PageCreator.getLastShapeLayer();
                    List<AbstractShape> ashapes = sl.getClosestShapes(lastStartPoint);
                    for(AbstractShape as : ashapes){
                        if(as instanceof Line | as instanceof Curve){
                            sl.updatePoint2D(as, lastStartPoint.getStartPoint(), new_param);
                        }
                    }
                    lastStartPoint.setStartPoint(new_param);                    
                }
            }
            
            repaint();
        }
        
    }
    
    public void vtdMouseMoved(java.awt.event.MouseEvent evt){
//        if(PageCreator.getLastShapeLayer() != null){
//            if(shapeSelected==ShapeSelection.None){
//
//            }else if(evt.getButton() == 2 && shapeSelected==ShapeSelection.Line){
//                if(lastSharedPoint!=null){
//                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
//                    ShapeLayer sl = PageCreator.getLastShapeLayer();
//                    List<AbstractShape> ashapes = sl.getClosestShapes(lastSharedPoint);
//                    for(AbstractShape as : ashapes){
//                        if(as instanceof Line | as instanceof Curve){
//                            sl.updatePoint2D(as, lastSharedPoint.getStartPoint(), new_param);
//                        }
//                    }
//                    lastSharedPoint.setStartPoint(new_param);
//                }
//            }else if(evt.getButton() == 2 && shapeSelected==ShapeSelection.Curve){
//                if(lastSharedPoint!=null){
//                    Point2D new_param = new Point2D.Float(evt.getX(), evt.getY());
//                    ShapeLayer sl = PageCreator.getLastShapeLayer();
//                    List<AbstractShape> ashapes = sl.getClosestShapes(lastSharedPoint);
//                    for(AbstractShape as : ashapes){
//                        if(as instanceof Line | as instanceof Curve){
//                            sl.updatePoint2D(as, lastSharedPoint.getStartPoint(), new_param);
//                        } 
//                    }
//                    lastSharedPoint.setStartPoint(new_param);
//                }
//            }
//        }
    }
    // </editor-fold>
    
}
