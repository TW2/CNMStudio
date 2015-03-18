/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class DualLock {
    
    private Key DES_key, Blowfish_key;
    
    public DualLock(){
        init();
    }
    
    private void init(){
        try {
            generateDesKey();
            generateBlowfishKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(DualLock.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="DES">
    
    public void setDesKey(Key DES_key) {
        this.DES_key = DES_key;
    }
    
    public Key getDesKey(){
        return DES_key;
    }
    
    public void generateDesKey() throws NoSuchAlgorithmException{
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        DES_key = keyGen.generateKey();
    }
    
    public byte[] cryptDes(byte[] bytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, DES_key);
        return cipher.doFinal(bytes);
    }
    
    public byte[] cryptDes(String plaintext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        return cryptBlowfish(plaintext.getBytes());
    }
    
    public byte[] decryptDesInBytes(byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, DES_key);
        return cipher.doFinal(ciphertext);
    }
    
    public String decryptDesInString(byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        return new String(decryptBlowfishInBytes(ciphertext));
    }
    
    /**
     * Retourne toutes les informations de la clé sous forme d'un tableau de
     * bytes. Elle peut ainsi être stockée puis reconstruite ultérieurement en
     * utilisant la méthode setSecretKey(byte[] keyData)
     * @return La clé encodée
     */
    public byte[] getSecretDesKeyInBytes() {
        return DES_key.getEncoded();
    }
    
    public String getSecretDesKey(){
        String string = new String(getSecretDesKeyInBytes());
        return string;
    }
    
    /**
     * Permet de reconstruire la clé secrète à partir de ses données, stockées 
     * dans un tableau de bytes.
     * @param keyData
     */
    public void setSecretDesKey(byte[] keyData) {
        DES_key = new SecretKeySpec(keyData, "DES");
    }
    
    public void setSecretDesKey(String string){
        setSecretDesKey(string.getBytes());
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Blowfish">
    
    public void setBlowfishKey(Key Blowfish_key) {
        this.Blowfish_key = Blowfish_key;
    }
    
    public Key getBlowfishKey(){
        return Blowfish_key;
    }
    
    public void generateBlowfishKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
        keyGen.init(128);
        Blowfish_key = keyGen.generateKey();
    }
    
    public byte[] cryptBlowfish(byte[] bytes) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, Blowfish_key);
        return cipher.doFinal(bytes);
    }
    
    public byte[] cryptBlowfish(String plaintext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        return cryptBlowfish(plaintext.getBytes());
    }
    
    public byte[] decryptBlowfishInBytes(byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.DECRYPT_MODE, Blowfish_key);
        return cipher.doFinal(ciphertext);
    }
    
    public String decryptBlowfishInString(byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        return new String(decryptBlowfishInBytes(ciphertext));
    }
    
    /**
     * Retourne toutes les informations de la clé sous forme d'un tableau de
     * bytes. Elle peut ainsi être stockée puis reconstruite ultérieurement en
     * utilisant la méthode setSecretKey(byte[] keyData)
     * @return La clé encodée
     */
    public byte[] getSecretBlowfishKeyInBytes() {
        return Blowfish_key.getEncoded();
    }
    
    public String getSecretBlowfishKey(){
        String string = new String(getSecretBlowfishKeyInBytes());
        return string;
    }
    
    /**
     * Permet de reconstruire la clé secrète à partir de ses données, stockées 
     * dans un tableau de bytes.
     * @param keyData
     */
    public void setSecretBlowfishKey(byte[] keyData) {
        Blowfish_key = new SecretKeySpec(keyData, "Blowfish");
    }
    
    public void setSecretBlowfishKey(String string){
        setSecretBlowfishKey(string.getBytes());
    }
    
    // </editor-fold>
    
    //**************************************************************************
    //********* APPLICATION - ADAPTATION
    //**************************************************************************
    
    //--------------------------------------------------------------------------
    //--------- IMAGE
    //--------------------------------------------------------------------------
    
    /**
     * Encode une image.
     * @param bi Une image
     * @return Un flux encodé/crypté
     * @throws IOException 
     */
    public String encodeImage(BufferedImage bi) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] bytes = baos.toByteArray();
        String value = Base64.encodeBytes(bytes);
        return value;
    }
    
    public BufferedImage decodeImage(String s) throws IOException{
        byte[] bytes = Base64.decode(s);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedImage img = ImageIO.read(bais);
        return img;
    }
    
    /**
     * Encode une image (avec une sécurité).
     * @param bi Une image
     * @return Un flux encodé/crypté
     * @throws IOException 
     */
    public String encodeSecureImage(BufferedImage bi) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        byte[] bytes = baos.toByteArray();
        String value = Base64.encodeBytes(bytes);
        byte[] bytesofkey = getSecretBlowfishKeyInBytes();
        String valueofkey = Base64.encodeBytes(bytesofkey);
        return value.substring(0, 4)+valueofkey+value.substring(4);
    }
    
    //--------------------------------------------------------------------------
    //--------- CHAINE
    //--------------------------------------------------------------------------
    
    /**
     * Encode et sécurise une chaine de caractère (avec deux sécurités).
     * @param s Une chaine
     * @return Un flux encodé/crypté
     */
    public String encodeSecureString(String s){
        String value = null;
        try {
            String commonsecurity = getSecretDesKey()+"-"+s;
            byte[] blowing = cryptBlowfish(commonsecurity);
            value = Base64.encodeBytes(blowing);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(DualLock.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return value;
    }
}
