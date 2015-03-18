/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
//import org.xiph.libogg.ogg_packet;
//import org.xiph.libogg.ogg_page;
//import org.xiph.libogg.ogg_stream_state;
//import org.xiph.libvorbis.vorbis_block;
//import org.xiph.libvorbis.vorbis_comment;
//import org.xiph.libvorbis.vorbis_dsp_state;
//import org.xiph.libvorbis.vorbis_info;
//import org.xiph.libvorbis.vorbisenc;

/**
 * Modified source from xiph.org (OggVorbisEncoder example)
 * @author Yves
 */
public class Audio {
    
    OggRecorder or = new OggRecorder();
    OggPlayer op = new OggPlayer();
    private Thread recorder = null;
    private Thread player = null;
        
    JButton remoteRecButton = new JButton();
    JButton remotePlayButton = new JButton();
    JButton remotePauseButton = new JButton();
    JButton remoteStopButton = new JButton();
    
    public Audio(){
        
    }
    
    public void startRecording(){        
        or.startWav();
        recorder = new Thread(or);
        recorder.start();
    }
    
    public void launchTranscode(){
        or.startOgg();
        recorder = new Thread(or);
        recorder.start();
    }
    
    public void stopRecording(){
        or.stopWav();
        if(recorder != null){
            recorder.interrupt();
            recorder = null;
//            launchTranscode();
        }        
    }
    
    public void startPlaying(){
        op.startOgg();
        player = new Thread(new OggPlayer());
        player.start();
    }
    
    public void pausePlaying(){
        if(player.isInterrupted()==false){
            player.interrupt();
        }else{
            player.start();
        }        
    }
    
    public void stopPlaying(){
        op.stopOgg();
        if(player != null){
            player.interrupt();
            player = null;
        }
        
    }
    
    //==========================================================================
    
    private File WAVFile = null, OGGfile = null, PlayFile = null;
    
    public void setWaveFile(String path){
        WAVFile = new File(path);
    }
    
    public void setOggFile(String path){
        OGGfile = new File(path);
    }
    
    public void setPlayFile(String path){
        PlayFile = new File(path);
    }
    
    //==========================================================================
        
    public void setRemoteRecButton(JButton b){
        remoteRecButton = b;
    }

    public void setRemotePlayButton(JButton b){
        remotePlayButton = b;
    }

    public void setRemotePauseButton(JButton b){
        remotePauseButton = b;
    }

    public void setRemoteStopButton(JButton b){
        remoteStopButton = b;
    }
    
    //==========================================================================
    
    public class OggRecorder implements Runnable {
        
        boolean isWAVStarted = false;
        boolean isOGGStarted = false;
        
        public OggRecorder(){
            
        }
        
        public void startWav(){
            isWAVStarted = true;
            remoteRecButton.setEnabled(false);
            remotePlayButton.setEnabled(false);
            remotePauseButton.setEnabled(false);
        }
        
        public void stopWav(){
            if(isWAVStarted == true){
                finishRecordingWAV();
                isWAVStarted = false;
                remoteRecButton.setEnabled(true);
                remotePlayButton.setEnabled(true);
                remotePauseButton.setEnabled(true);
            }
        }
                
        public void startOgg(){
            isOGGStarted = true;
            remoteRecButton.setEnabled(false);
            remotePlayButton.setEnabled(false);
            remotePauseButton.setEnabled(false);
            remoteStopButton.setEnabled(false);
        }
        
        public void stopOgg(){
            isOGGStarted = false;
            remoteRecButton.setEnabled(true);
            remotePlayButton.setEnabled(true);
            remotePauseButton.setEnabled(true);
            remoteStopButton.setEnabled(true);
            if(recorder != null){
                recorder.interrupt();
                recorder = null;
            }
        }

        @Override
        public void run() {
            if(isWAVStarted == true){
                startRecordingWAV();
            }
            if(isOGGStarted == true){                
//                transcodeWAVtoOGG();
            }
        }
        
        //######################################################################
        
        // record duration, in milliseconds
        private static final long RECORD_TIME = 60000;  // 1 minute 
        // format of audio file
        private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE; 
        // the line from which audio data is captured
        private TargetDataLine line;



        /**
         * Defines an audio format
         */
        private AudioFormat getAudioFormat() {
            float sampleRate = 16000;
            int sampleSizeInBits = 8;
            int channels = 2;
            boolean signed = true;
            boolean bigEndian = true;
            AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                                 channels, signed, bigEndian);
            return format;
        }

        /**
         * Captures the sound and record into a WAV file
         * @param path
         */
        private void startRecordingWAV() {

            try {
                AudioFormat format = getAudioFormat();
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                // checks if system supports the data line
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Line not supported");
                    System.exit(0);
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();   // start capturing

                System.out.println("Start capturing...");

                AudioInputStream ais = new AudioInputStream(line);

                System.out.println("Start recording...");

                // start recording
                AudioSystem.write(ais, fileType, WAVFile);

            } catch (LineUnavailableException | IOException ex) {
            }
        }

        /**
         * Closes the target data line to finish capturing and recording
         */
        private void finishRecordingWAV() {
            line.stop();
            line.close();
            System.out.println("Finished");
        }

//        private vorbisenc encoder;
//        private ogg_stream_state os;    // take physical pages, weld into a logical stream of packets
//        private ogg_page og;            // one Ogg bitstream page.  Vorbis packets are inside
//        private ogg_packet op;          // one raw packet of data for decode
//        private vorbis_info vi;         // struct that stores all the static vorbis bitstream settings
//        private vorbis_comment vc;      // struct that stores all the user comments
//        private vorbis_dsp_state vd;    // central working state for the packet->PCM decoder
//        private vorbis_block vb;        // local working space for packet->PCM decode
//
//        private final int READ = 1024;
//        private final byte[] readbuffer = new byte[READ*4+44];
//
//        private final int page_count = 0;
//        private final int block_count = 0;
//
//        private boolean transcodeWAVtoOGG(){
//            boolean eos = false;
//
//            vi = new vorbis_info();
//
//            encoder = new vorbisenc();
//
//            if ( !encoder.vorbis_encode_init_vbr( vi, 2, 44100, .3f ) ) {
//                System.out.println( "Failed to Initialize vorbisenc" );
//                return false;
//            }
//
//            vc = new vorbis_comment();
//            vc.vorbis_comment_add_tag( "ENCODER", "Java Vorbis Encoder" );
//
//            vd = new vorbis_dsp_state();
//
//            if ( !vd.vorbis_analysis_init( vi ) ) {
//                System.out.println( "Failed to Initialize vorbis_dsp_state" );
//                return false;
//            }
//
//            vb = new vorbis_block( vd );
//
//            java.util.Random generator = new java.util.Random();  // need to randomize seed
//            os = new ogg_stream_state( generator.nextInt(256) );
//
//            System.out.print( "Writing header." );
//            ogg_packet header = new ogg_packet();
//            ogg_packet header_comm = new ogg_packet();
//            ogg_packet header_code = new ogg_packet();
//
//            vd.vorbis_analysis_headerout( vc, header, header_comm, header_code );
//
//            os.ogg_stream_packetin( header); // automatically placed in its own page
//            os.ogg_stream_packetin( header_comm );
//            os.ogg_stream_packetin( header_code );
//
//            og = new ogg_page();
//            op = new ogg_packet();
//
//            try {
//
//                FileOutputStream fos = new FileOutputStream( OGGfile );
//
//                while( !eos ) {
//
//                    if ( !os.ogg_stream_flush( og ) )
//                            break;
//
//                    fos.write( og.header, 0, og.header_len );
//                    fos.write( og.body, 0, og.body_len );
//                    System.out.print( "." );
//                }
//                System.out.print(  "Done.\n" );
//
//                FileInputStream fin = new FileInputStream( WAVFile );
//
//                System.out.print( "Encoding." );
//                while ( !eos ) {
//
//                    int i;
//                    int bytes = fin.read( readbuffer, 0, READ*4 ); // stereo hardwired here
//
//                    int break_count = 0;
//
//                    if ( bytes==0 ) {
//
//                        // end of file.  this can be done implicitly in the mainline,
//                        // but it's easier to see here in non-clever fashion.
//                        // Tell the library we're at end of stream so that it can handle
//                        // the last frame and mark end of stream in the output properly
//
//                        vd.vorbis_analysis_wrote( 0 );
//
//                    } else {
//
//                        // data to encode
//
//                        // expose the buffer to submit data
//                        float[][] buffer = vd.vorbis_analysis_buffer( READ );
//
//                        // uninterleave samples
//                        for ( i=0; i < bytes/4; i++ ) {
//                                buffer[0][vd.pcm_current + i] = ( (readbuffer[i*4+1]<<8) | (0x00ff&(int)readbuffer[i*4]) ) / 32768.f;
//                                buffer[1][vd.pcm_current + i] = ( (readbuffer[i*4+3]<<8) | (0x00ff&(int)readbuffer[i*4+2]) ) / 32768.f;
//                        }
//
//                        // tell the library how much we actually submitted
//                        vd.vorbis_analysis_wrote( i );
//                    }
//
//                    // vorbis does some data preanalysis, then divvies up blocks for more involved 
//                    // (potentially parallel) processing.  Get a single block for encoding now
//
//                    while ( vb.vorbis_analysis_blockout( vd ) ) {
//
//                        // analysis, assume we want to use bitrate management
//
//                        vb.vorbis_analysis( null );
//                        vb.vorbis_bitrate_addblock();
//
//                        while ( vd.vorbis_bitrate_flushpacket( op ) ) {
//
//                            // weld the packet into the bitstream
//                            os.ogg_stream_packetin( op );
//
//                            // write out pages (if any)
//                            while ( !eos ) {
//
//                                if ( !os.ogg_stream_pageout( og ) ) {
//                                        break_count++;
//                                        break;
//                                }
//
//                                fos.write( og.header, 0, og.header_len );
//                                fos.write( og.body, 0, og.body_len );
//
//                                // this could be set above, but for illustrative purposes, I do
//                                // it here (to show that vorbis does know where the stream ends)
//                                if ( og.ogg_page_eos() > 0 )
//                                        eos = true;
//                            }
//                        }
//                    }
//                    System.out.print( "." );
//                }
//
//                fin.close();
//                fos.close();
//
//                System.out.print( "Done.\n" );
//
//                stopOgg();
//
//                return true;
//            } catch (IOException e) { System.out.println( "\n" + e ); e.printStackTrace(System.out); }
//
//            stopOgg();
//
//            return false;
//        }
        
        //######################################################################
        
    }
    
    public class OggPlayer implements Runnable {
        
        boolean isStarted = false;
        
        public OggPlayer(){
            
        }
        
        public void startOgg(){
            isStarted = true;
        }
        
        public void stopOgg(){
            isStarted = false;
        }

        @Override
        public void run() {
            if(isStarted == true){
                playOGG();
            }
        }
        
        //######################################################################
        
        private AudioFormat getOutFormat(AudioFormat inFormat) {
            final int ch = inFormat.getChannels();
            final float rate = inFormat.getSampleRate();
            return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
        }

        private void stream(AudioInputStream in, SourceDataLine line) 
            throws IOException {
            final byte[] buffer = new byte[4096];
            for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
                line.write(buffer, 0, n);
            }
        }

        private void playOGG(){

            if(PlayFile.exists()){
                try (final AudioInputStream in = AudioSystem.getAudioInputStream(PlayFile)) {

                    final AudioFormat outFormat = getOutFormat(in.getFormat());
                    final Info info = new Info(SourceDataLine.class, outFormat);

                    try (final SourceDataLine sdline =
                             (SourceDataLine) AudioSystem.getLine(info)) {

                        if (sdline != null) {
                            sdline.open(outFormat);
                            sdline.start();
                            stream(AudioSystem.getAudioInputStream(outFormat, in), sdline);
                            sdline.drain();
                            sdline.stop();
                        }
                    }

                } catch (UnsupportedAudioFileException 
                       | LineUnavailableException 
                       | IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            stopOgg();
        }
        
        //######################################################################
        
    }
    
    public void convertToMp3(){
        
    }
    
}
