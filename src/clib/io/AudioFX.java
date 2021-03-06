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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.SwingUtilities;

/**
 *
 * @author Phil
 */
public class AudioFX implements Runnable {
    
    private String playpath, recordpath;
    private Thread record, listen;
    private boolean inListening = false;
    
    public AudioFX(){
        
    }
    
    public void setListenPath(String playpath){
        this.playpath = playpath;
    }
    
    public void setRecordPath(String recordpath){
        this.recordpath = recordpath;
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
    
    private String replaceSlash(String winpath){
        return winpath.replace('\\', '/');
    }
    
    private void playFX() {
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); //Force l'utilisation de JavaFX dans Swing
            media = new Media("file:///"+replaceSlash(playpath));
            mplayer = new MediaPlayer(media);
            mplayer.play();
        });
    }
    
    private void stopFX(){
        SwingUtilities.invokeLater(() -> {
            new JFXPanel(); //Force l'utilisation de JavaFX dans Swing
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
