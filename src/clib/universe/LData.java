/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.universe;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Yves
 */
public class LData {
    
    private final Map<String, String> dataMap = new HashMap<>();
    
    public LData(){
        
    }
    
    public void setCopyright(boolean copyright){
        if(copyright == true){
            dataMap.put("Copyright", "readonly");
        }else{
            dataMap.put("Copyright", "readwrite");
        }
    }
    
    public String getCopyright(){
        return dataMap.get("Copyright");
    }
    
    public void setReader(String reader){
        if(reader.equalsIgnoreCase("CNMReader")){
            dataMap.put("Reader", "free");
        }else{
            dataMap.put("Reader", reader);
        }
    }
    
    public String getReader(){
        return dataMap.get("Reader");
    }
    
    public Map<String, String> getLData(){
        return dataMap;
    }
    
}
