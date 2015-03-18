/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clib.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Phil
 */
public class AudioFX implements Runnable {
    
    private String playpath, recordpath;
    private Thread record, listen;
    private boolean inRecording = false, inListening = false;
    private ByteArrayOutputStream out;
    
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
    private void play() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
        File file = new File(playpath);
        AudioInputStream in = AudioSystem.getAudioInputStream(file);        
        Clip clip = AudioSystem.getClip();        
        clip.open(in);
        clip.start();
    }
    
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
    // RECORD """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    private void record() throws LineUnavailableException, FileNotFoundException, IOException{
        
        AudioFormat format = new AudioFormat(44100f, 16, 2, true, true); //WAV 16 bits stereo 48khz
        
//        TargetDataLine line;
//        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);   // format is an AudioFormat object
//        line = (TargetDataLine) AudioSystem.getLine(info);
        
        TargetDataLine line = (TargetDataLine)getMicEntries().get(0);
        
        line.open(format);
        
        // Assume that the TargetDataLine, line, has already
        // been obtained and opened.
        out = new ByteArrayOutputStream();
        int numBytesRead;
        byte[] data = new byte[line.getBufferSize() / 5];

        // Begin audio capture.
        line.start();

        // Here, stopped is a global boolean set by another thread.
        while (record.isAlive()) {
           // Read the next chunk of data from the TargetDataLine.
           numBytesRead =  line.read(data, 0, data.length);
           // Save this chunk of data.
           out.write(data, 0, numBytesRead);
        }
    }
    
    public void recordStart(){
        record = new Thread();
        record.start();
        inRecording = true;
        run();
    }
    
    public void recordStop(){
        if(record != null && record.isAlive()){
            FileOutputStream outputStream;            
            try {
                outputStream = new FileOutputStream (recordpath);
                out.writeTo(outputStream);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(AudioFX.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(AudioFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            record.interrupt();
            record = null;
        }        
    }
    
    private List<Line> getMicEntries() throws LineUnavailableException{
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        List<Line> lines = new ArrayList<>();
        
        for (Mixer.Info info: mixerInfos){
            Mixer m = AudioSystem.getMixer(info);
            
            Line.Info[] lineInfos = m.getTargetLineInfo();
            
            for (Line.Info lineInfo : lineInfos){
                if(info.getName().toLowerCase().contains("microphone")){
                    Line line = m.getLine(lineInfo);
                    lines.add(line);
                }
            }
        }
        return lines.size()>0 ? lines : null;
    }
    
    public void viewMixerInfo() throws LineUnavailableException{
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info: mixerInfos){
            Mixer m = AudioSystem.getMixer(info);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            
            for (Line.Info lineInfo:lineInfos){
                System.out.println (info.getName()+"---"+lineInfo);
                Line line = m.getLine(lineInfo);
                System.out.println("\t-----"+line);
            }
            
            lineInfos = m.getTargetLineInfo();
            
            for (Line.Info lineInfo:lineInfos){
                System.out.println (m+"---"+lineInfo);
                Line line = m.getLine(lineInfo);
                System.out.println("\t-----"+line);
            }
        }
    }

    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    // THREAD """"""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    //""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""""
    @Override
    public void run() {
        if(inRecording == true){
            try {
                record();
            } catch (LineUnavailableException | IOException ex) {
                Logger.getLogger(AudioFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            recordStop();
            inRecording = false;
        }
        
        if(inListening == true){
            try {
                play();
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(AudioFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            listenStop();
            inListening = false;
        }
    }
    
}
