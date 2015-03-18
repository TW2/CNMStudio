/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer;

/**
 *
 * @author Yves
 */
public class AudioLayer {
    
    private String name = "ID";
    private String mp3path = "";
    
    public AudioLayer(){
        
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setMP3Path(String mp3path){
        this.mp3path = mp3path;
    }
    
    public String getMP3Path(){
        return mp3path;
    }
    
}
