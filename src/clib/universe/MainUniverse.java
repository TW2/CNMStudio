/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.universe;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Yves
 */
public class MainUniverse {
    
    private String firstName = "";
    private String lastName = ""; //Last name or organization
    private String MAC_Address = "";
    private LData LOL = new LData();
    
    public MainUniverse(){
        
    }
    
    public void setFirstName(String firstName){
        this.firstName = firstName;
    }
    
    public String getFirstName(){
        return firstName;
    }
    
    public void setLastName(String lastName){
        this.lastName = lastName;
    }
    
    public String getLastName(){
        return lastName;
    }
    
    public void setMacAddress(){
        try {
            searchElements();
        } catch (UnknownHostException | SocketException ex) {
        }
    }
    
    public String getMacAddress(){
        return MAC_Address;
    }
    
    public void setLOL(LData LOL){
        this.LOL = LOL;
    }
    
    public LData getLOL(){
        return LOL;
    }
    
    private void searchElements() throws UnknownHostException, SocketException{
        InetAddress ip;
        ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        MAC_Address = sb.toString();
    }
}
