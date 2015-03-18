/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

/**
 * <p>Le but de cette classe est de sécuriser le logiciel en vue du piratage.<br /><br />
 * EasyLock doit créer/vérifier un hash à partir de l'adresse MAC et de quelques autres paramètres par ce principe :
 * <ul>
 * <li>Récupération de l'adresse MAC.</li>
 * <li>Obtention du prénom.</li>
 * <li>Obtention du nom de famille.</li>
 * <li>Chiffrement du Prénom + Nom + Adresse MAC séparé chacun par un tiret qui formeront l'"hash de référence".</li>
 * <li>Interrogation de la BDD en ligne et transmission de Nom Prénom et "hash de référence"</li>
 * <li>Retour de la BDD en ligne qui nous transmet l'"hash d'enregistrement" (préaleblement communiqué à l'achat).</li>
 * <li>Vérification de l'"hash d'enregistrement" local avec l'"hash d'enregistement" distant.</li>
 * <li>Renvoyer OK ou Locked.</li>
 * </ul>
 * </p>
 * @author Yves
 */
public class EasyLock {
    
    private String firstname = "", lastname = "", mac = "";
    private String localReference = "";
    private String localRegister = "", distantRegister = null;
    private State state = State.Locked;
    
    public enum State{
        OK, Locked;
    }
    
    /**
     * Crée un nouvel objet EasyLock.
     */
    public EasyLock(){
        
    }
    
    //**************************************************************************
    // Récupération de l'état
    //**************************************************************************
    
    public State getState(){
        return state;
    }
    
    //**************************************************************************
    // Récupération Prénom, Nom, Adresse MAC
    //**************************************************************************
    
    public void setFirstName(String firstname){
        this.firstname = firstname;
    }
    
    public String getFirstName(){
        return firstname;
    }
    
    public void setLastName(String lastname){
        this.lastname = lastname;
    }
    
    public String getLastName(){
        return lastname;
    }
    
    private void searchForMac(){
        try {
            String firstInterface = null;
            Map<String, String> addressByNetwork = new HashMap<>();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            
            while(networkInterfaces.hasMoreElements()){
                NetworkInterface network = networkInterfaces.nextElement();
                
                byte[] bmac = network.getHardwareAddress();
                if(bmac != null){
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < bmac.length; i++){
                        sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
                    }
                    
                    if(sb.toString().isEmpty()==false){
                        addressByNetwork.put(network.getName(), sb.toString());
                        System.out.println("Address = "+sb.toString()+" @ ["+network.getName()+"] "+network.getDisplayName());
                    }
                    
                    if(sb.toString().isEmpty()==false && firstInterface == null){
                        firstInterface = network.getName();
                    }
                }
            }
            
            if(firstInterface != null){
                mac = addressByNetwork.get(firstInterface);
            }else{
                mac = "";
            }
        } catch (SocketException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public String getMacAddress(){
        return mac;
    }
    
    //**************************************************************************
    // Méthode simple de hash
    //**************************************************************************
    
    private Key Blowfish_key;
    
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
    // Création de l'hash de référencement
    //**************************************************************************
    
    private void createRefHash(){
        if(firstname.isEmpty() == false && lastname.isEmpty() == false){
            searchForMac();
            if(mac.isEmpty() == false){
                try {
                    setSecretBlowfishKey(firstname+"/"+lastname);
                    byte[] bytes = cryptBlowfish(firstname+"-"+lastname+"-"+mac);
                    localReference = new String(bytes);
                    System.out.println(firstname);
                    System.out.println(lastname);
                    System.out.println(mac);
                    System.out.println(localReference);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }else{
                JOptionPane.showMessageDialog(null, "A problem has been detected with your system.");
            }
        }else{
            JOptionPane.showMessageDialog(null, "Please type your firstname and your lastname is the appropriate section.");
        }
    }
    
    //**************************************************************************
    // BDD - Transmission
    //**************************************************************************
    
    private boolean uploadData(){
        
        return false;
    }
    
    //**************************************************************************
    // BDD - Récupération
    //**************************************************************************
    
    private boolean downloadData(){
        
        return false;
    }
    
    //**************************************************************************
    // Vérification local et distant et changement d'état
    //**************************************************************************
    
    private void verifyHash(){
        if(localRegister.equals(distantRegister)){
            state = State.OK;
        }else{
            state = State.Locked;
        }        
    }
    
    //**************************************************************************
    // Méthode tout-en-un pour vérifier
    //**************************************************************************
    
    public State verify(String firstname, String lastname){
        this.firstname = firstname;
        this.lastname = lastname;
        return verify();
    }
    
    public State verify(){
        createRefHash();
        uploadData();
        downloadData();
        verifyHash();
        if(state == State.OK){
            JOptionPane.showMessageDialog(null, "Your licence is OK.\nHave fun !");
        }else{
            JOptionPane.showMessageDialog(null, "Your licence is not OK.\nPlease buy a licence.");
        }
        return state;
    }
}
