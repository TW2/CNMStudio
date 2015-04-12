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
package clib.io.drm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Antoine
 */
public class ExternalInfo {

    private IDRM drm = null;
    
    private Period period = new Period();
//    private long starttime = -1;
//    private long endtime = -1;
    
    private Duration duration = new Duration();
//    private long duration = -1;
//    private long elapsedtime = -1;
    
    private Count count = new Count();
//    private int count = -1;
//    private int times = -1;
    
    private String MAC = " ";
    private String familyName = " ";
    
    public ExternalInfo() {
        loadPlugins();
    }
    
    public void setDRM(IDRM drm){
        this.drm = drm;
        period = drm.getPeriod();
        duration = drm.getDuration();
        count = drm.getCount();
    }
    
    public IDRM getDRM(){
        return drm;
    }
    
    public void setMAC(String MAC){
        this.MAC = MAC;
        if(this.MAC.isEmpty()){
            searchForMac();
        }
    }
    
    public String getMAC(){
        return MAC;
    }
    
    public void setFamilyName(String familyName){
        this.familyName = familyName;
    }
    
    public String getFamilyName(){
        return familyName;
    }
    
    //Plugins
    private List<IDRM> pluginList = new ArrayList<>();
    
    /**
     * Set plug-in to use (DRM to use)
     * @param name The name of this DRM
     */
    public void usePlugin(String name){
        for(IDRM idrm : pluginList){
            if(idrm.getName().equalsIgnoreCase(name)){
                this.drm = idrm;
            }
        }
    }
    
    /**
     * Load all plug-ins (load all DRMs)
     */
    private void loadPlugins(){
        File f = new File(getApplicationDirectory()+File.separator+"plugins");
        if(f.exists()){
            StandardPluginService pluginService = PluginServiceFactory.createPluginService(f);
            pluginList = pluginService.initPlugins();
        }
    }
    
    private String getApplicationDirectory(){
        if(System.getProperty("os.name").equalsIgnoreCase("Mac OS X")){
            java.io.File file = new java.io.File("");
            return file.getAbsolutePath();
        }
        String path = System.getProperty("user.dir");
        if(path.toLowerCase().contains("jre")){
            File f = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toString()
                    .substring(6));
            path = f.getParent();
        }
        return path;
    }
    
    //MAC address
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
                MAC = addressByNetwork.get(firstInterface);
            }else{
                MAC = " ";
            }
        } catch (SocketException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    public void reader(String parent) throws DRMException, FileNotFoundException, IOException{
        FileReader fr = new FileReader(parent + File.separator + drm.getExternalFileName());
        BufferedReader br = new BufferedReader(fr);
        String encrypted = br.readLine();
        String toRead = drm.decryptExternal(encrypted);
        String[] table = toRead.split(";");
        period.importStart(table[0]);
        period.importEnd(table[1]);
        duration.importDuration(table[2]);
        duration.importElapsedTime(table[3]);
        count.importTotal(table[4]);
        count.importElapsed(table[5]);
        String drmName = table[6];
        String drmMedia = table[7];
        MAC = table[8];
        familyName = table[9];
        
        if(drmName.equalsIgnoreCase(drm.getName())==false | drmMedia.equalsIgnoreCase(drm.getMedia())==false){
            throw new DRMException();
        }
    }
    
    public void writer(String parent) throws FileNotFoundException, UnsupportedEncodingException, DRMException{
        try (PrintWriter writer = new PrintWriter(parent + File.separator + drm.getExternalFileName(), "UTF-8")) {
            String toEncrypt = 
                    period.exportStart() + ";" + 
                    period.exportEnd() + ";" + 
                    duration.exportDuration() + ";" +
                    duration.exportElapsedTime() + ";" +
                    count.exportTotal() + ";" +
                    count.exportElapsed() + ";" + 
                    drm.getName() + ";" +
                    drm.getMedia() + ";" +
                    MAC + ";" + 
                    familyName;
            
            String encrypted = drm.encryptExternal(toEncrypt);
            
            writer.print(encrypted);
        }
    }
    
}
