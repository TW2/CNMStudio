/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io;

import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.layer.vector.AbstractShape;
import clib.layer.vector.Curve;
import clib.layer.vector.Line;
import clib.layer.vector.Move;
import clib.universe.LData;
import clib.universe.MainUniverse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

/**
 *
 * @author Yves
 */
public class CNMWriter {
    
    private PageLayer page = new PageLayer();
    private ImageLayer image = new ImageLayer();
    private List<ShapeLayer> shapes = new ArrayList<>();
    private List<TextLayer> texts = new ArrayList<>();
    private MainUniverse main = new MainUniverse();
    
    // Blowfish pris sur :
    // ftp://ftp-developpez.com/java/sources/MyBlowfish.java
    // http://java.developpez.com/sources/?page=cryptage#blowfish
    private static final int KEY_SIZE = 128;  // [32..448]
    private Key secretKey;
    
    public CNMWriter(){
        
    }
    
    // <editor-fold defaultstate="collapsed" desc="Blowfish">
    
    public void setSecretKey(Key secretKey) {
        this.secretKey = secretKey;
    }
    
    public Key getSecretKey(){
        return secretKey;
    }
    
    /**
     * Retourne toutes les informations de la clé sous forme d'un tableau de
     * bytes. Elle peut ainsi être stockée puis reconstruite ultérieurement en
     * utilisant la méthode setSecretKey(byte[] keyData)
     * @return La clé encodée
     */
    public byte[] getSecretKeyInBytes() {
        return secretKey.getEncoded();
    }
    
    /**
     * Permet de reconstruire la clé secrète à partir de ses données, stockées 
     * dans un tableau de bytes.
     * @param keyData
     */
    public void setSecretKey(byte[] keyData) {
        secretKey = new SecretKeySpec(keyData, "Blowfish");
    }
    
    public void generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
            keyGen.init(KEY_SIZE);
            secretKey = keyGen.generateKey();
        }catch (NoSuchAlgorithmException e) {System.out.println(e);}
    }
    
    public byte[] crypt(byte[] plaintext) {
        try {
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(plaintext);
        }catch (NoSuchAlgorithmException | NoSuchPaddingException 
                | InvalidKeyException | IllegalBlockSizeException 
                | BadPaddingException e) {System.out.println(e);}
        return null;
    }
    
    public byte[] crypt(String plaintext) {
        return crypt(plaintext.getBytes());
    }
    
    public byte[] decryptInBytes(byte[] ciphertext) {
        try {
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(ciphertext);
        }catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException e) {System.out.println(e);}
        return null;
    }
    
    public String decryptInString(byte[] ciphertext) {
        return new String(decryptInBytes(ciphertext));
    }
    
    // </editor-fold>
    
    public void setPageLayer(PageLayer page){
        this.page = page;
    }
    
    public PageLayer getPageLayer(){
        return page;
    }
    
    public void setImageLayer(ImageLayer image){
        this.image = image;
    }
    
    public ImageLayer getImageLayer(){
        return image;
    }
    
    public void setShapeLayerList(List<ShapeLayer> shapes){
        this.shapes = shapes;
    }
    
    public List<ShapeLayer> getShapeLayerList(){
        return shapes;
    }
    
    public void setTextLayerList(List<TextLayer> texts){
        this.texts = texts;
    }
    
    public List<TextLayer> getTextLayerList(){
        return texts;
    }
    
    public void setMainUniverse(MainUniverse main){
        this.main = main;
    }
    
    public MainUniverse getMainUniverse(){
        return main;
    }
    
    public byte[] writeString(String something) throws UnsupportedEncodingException{
        return something.getBytes("UTF-8");
    }
    
    public byte[] writeInt(int something) throws UnsupportedEncodingException{
        return Integer.toString(something).getBytes("UTF-8");
    }
    
    public byte[] writeStringSecure(String something) throws UnsupportedEncodingException{
        System .out.println("Code = " + something);
        generateKey();
        byte[] ciphertext = crypt(something);
        System .out.println("ciphertext = " + new BigInteger(ciphertext));
        return ciphertext;
    }
    
    public void write(String path) throws FileNotFoundException, UnsupportedEncodingException, IOException{
        
        FileOutputStream fos = new FileOutputStream(path);       
        
        //[Header (Page data) – Strings and Integers]
        fos.write(writeString(page.getName()));
        fos.write(writeString("$"));
        fos.write(writeInt(page.getWidth()));
        fos.write(writeString("$"));
        fos.write(writeInt(page.getHeight()));
        
        //Separator
        fos.write(writeString("§"));
        
        //[Header (LOL contents) – Strings] [DRM]
        LData ldata = main.getLOL();
        Map<String, String> dataMap = ldata.getLData();
        fos.write(writeString(ldata.getCopyright()));
        fos.write(writeString("$"));
        if(ldata.getReader().equalsIgnoreCase("free")){
            fos.write(writeString(ldata.getReader()));
        }else{
            fos.write(writeStringSecure(ldata.getReader()));
        }
        if(dataMap.size() > 2){            
            for(String s : dataMap.values()){
                if(s.equalsIgnoreCase("readonly") == false 
                        && s.equalsIgnoreCase("readwrite") == false
                        && s.equalsIgnoreCase(ldata.getReader()) == false){
                    fos.write(writeString("$"));
                    fos.write(writeString(s));
                }
            }
        }
        
        //Separator
        fos.write(writeString("§"));
        
        //[Header (Image contents) – Strings and Integers]
        fos.write(writeString(image.getName()));
        fos.write(writeString("$"));
        fos.write(writeInt(image.getWidth()));
        fos.write(writeString("$"));
        fos.write(writeInt(image.getHeight()));
        
        //Separator
        fos.write(writeString("§"));
        
        //[Array of Shape blocks as name + coordinates (M move + pts – L line + pts – C curve + pts)]
        int countShape = 0;
        for(ShapeLayer sl : shapes){
            if(countShape != 0){
                fos.write(writeString("$"));
            }
            fos.write(writeString(sl.getName()));
            fos.write(writeString("µ"));
            for(AbstractShape as : sl.getShapes()){                
                if(as instanceof Move){
                    Move move = (Move)as;
                    fos.write(writeString(
                            "M"+move.getStartPoint().getX()+","+move.getStartPoint().getY()));
                }else if(as instanceof Line){
                    Line line = (Line)as;
                    fos.write(writeString(
                            "L"+line.getEndPoint().getX()+","+line.getEndPoint().getY()));
                }else if(as instanceof Curve){
                    Curve curve = (Curve)as;
                    fos.write(writeString(
                            "C"+curve.getControlPoint_1().getX()+","+curve.getControlPoint_1().getY()+","
                            +curve.getControlPoint_2().getX()+","+curve.getControlPoint_2().getY()+","
                            +curve.getEndPoint().getX()+","+curve.getEndPoint().getY()));
                }
            }
            countShape += 1;
        }
        
        //Separator
        fos.write(writeString("§"));
        
        //[Array of Text blocks as name + languages (Iso3 for language + text)]
        int countText = 0;
        for(TextLayer tl : texts){
            if(countText != 0){
                fos.write(writeString("$"));
            }
            fos.write(writeString(tl.getCountry().getAlpha3()));
            fos.write(writeString("µ"));
            fos.write(writeString(tl.getText(tl.getCountry())));
            countText += 1;
        }
        
        //Separator
        fos.write(writeString("§"));
        
        fos.write(writeString(encodeImage(image.getImage())));
        
    }
    
    private String encodeImage(BufferedImage bi) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] bytes = baos.toByteArray();
        String value = Base64.encodeBytes(bytes);
        return value;
    }
    
}
