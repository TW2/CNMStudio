/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.universe;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Yves
 */
public class Universe {
    
    private static String universe = null;
    private static String star = null;
    
    // Blowfish pris sur :
    // ftp://ftp-developpez.com/java/sources/MyBlowfish.java
    // http://java.developpez.com/sources/?page=cryptage#blowfish
    private static final int KEY_SIZE = 128;  // [32..448]
    private Key secretKey;
    
    public Universe(){
        
    }
    
    public static boolean checkUniverse(MainUniverse main){
        if(main.getFirstName().isEmpty() == false 
                && main.getLastName().isEmpty() == false
                && main.getMacAddress().isEmpty() == false){
            try {
                String code1 = getFirst(main) + "-" + getSecond(main) + "-" + getThird(main);
                String code2 = getStar(main);
                if(code1.equalsIgnoreCase(universe)  && code2.equalsIgnoreCase(star)){
                    return true;
                }
            } catch (NoSuchAlgorithmException ex) {
            }
        }
        return false;
    }
    
    public static void fakeUniverse(MainUniverse main){
        if(main.getFirstName().isEmpty() == false 
                && main.getLastName().isEmpty() == false
                && main.getMacAddress().isEmpty() == false){
            try {
                universe = getFirst(main) + "-" + getSecond(main) + "-" + getThird(main);
                star = getStar(main);
            } catch (NoSuchAlgorithmException ex) {
            }
        }
    }
    
    public static String getFirst(MainUniverse main) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(main.getFirstName().getBytes());
        byte[] mdbytes = md.digest();
        StringBuilder hexString = new StringBuilder();
    	for (int i=0;i<mdbytes.length;i++) {
    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
        return hexString.substring(0, 4).toUpperCase();
    }
    
    public static String getSecond(MainUniverse main) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(main.getLastName().getBytes());
        byte[] mdbytes = md.digest();
        StringBuilder hexString = new StringBuilder();
    	for (int i=0;i<mdbytes.length;i++) {
    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
        return hexString.substring(10, 13).toUpperCase();
    }
    
    public static String getThird(MainUniverse main) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(main.getMacAddress().getBytes());
        byte[] mdbytes = md.digest();
        StringBuilder hexString = new StringBuilder();
    	for (int i=0;i<mdbytes.length;i++) {
    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
        return hexString.substring(4, 8).toUpperCase();
    }
    
    public static String getStar(MainUniverse main) throws NoSuchAlgorithmException{
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(main.getMacAddress().getBytes());
        byte[] mdbytes = md.digest();
        StringBuilder hexString = new StringBuilder();
    	for (int i=0;i<mdbytes.length;i++) {
    	  hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
    	}
        return hexString.toString().toLowerCase();
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
    
}
