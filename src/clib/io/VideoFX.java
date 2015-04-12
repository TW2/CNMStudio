/*
 * Copyright (C) 2015 Antoine
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package clib.io;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javax.swing.SwingUtilities;

/**
 *
 * @author Antoine
 */
public class VideoFX implements Runnable {

    private String playpath;
    private Thread listen;
    private boolean inListening = false;
    
    public VideoFX() {
        
    }

    public void setListenPath(String playpath){
        this.playpath = playpath;
    }
    
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    // LISTEN """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
           
    public void listenStart(){
        listen = new Thread();
        listen.start();
        inListening = true;
        run();
    }
    
    public void listenStop(){
        if(listen!= null && listen.isAlive()){
            listen.interrupt();
            listen = null;
        }        
    }
    
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    // JAVAFX """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    
    private Media media;
    private MediaPlayer mplayer = null;
    private JFXPanel fxPanel = null;
    private Scene scene;
    
    public JFXPanel prepareFX(int width, int height){
        SwingUtilities.invokeLater(() -> {
            fxPanel = new JFXPanel(); //Force l'utilisation de JavaFX dans Swing
            media = new Media("file:///"+replaceSlash(playpath));
            mplayer = new MediaPlayer(media);            
            scene = new Scene(new Group(), width, height);
            MediaView mediaView = new MediaView(mplayer);
            ((Group)scene.getRoot()).getChildren().add(mediaView);
            fxPanel.setScene(scene);        
        });
        return fxPanel;
    }
    
    private String replaceSlash(String winpath){
        return winpath.replace('\\', '/');
    }
    
    private void playFX() {
        SwingUtilities.invokeLater(() -> {
            if(mplayer != null){
                mplayer.play();
                mplayer = null;
            }
        });
    }
    
    private void stopFX(){
        SwingUtilities.invokeLater(() -> {
            if(mplayer != null){
                mplayer.stop();
                mplayer = null;
            }            
        });
    }
    
    

    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    // THREAD """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    @Override
    public void run() {
        if(inListening == true){
            playFX();
        }else{
            stopFX();
            listenStop();
            inListening = false;
        }
    }
}