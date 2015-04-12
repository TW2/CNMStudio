/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io.drm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * A service for our Plugin objects.
 * It is a code from James Selvakumar's tutorial on his blog : http://solitarygeek.com/java/a-simple-pluggable-java-application .
 * The goal was to do a plugin system.
 * @author Yves
 */
public class StandardPluginService {
    
    private static StandardPluginService pluginService;
    private final ServiceLoader<IDRM> serviceLoader;
 
    /**
     * Load all the classes in the classpath that have implemented the interface.
     */
    private StandardPluginService(){
        serviceLoader = ServiceLoader.load(IDRM.class);
    }
 
    /**
     * Get a service.
     * @return An initialized service.
     */
    public static StandardPluginService getInstance(){
        if(pluginService == null){
            pluginService = new StandardPluginService();
        }
        return pluginService;
    }
 
    public Iterator<IDRM> getPlugins(){
        return serviceLoader.iterator();
    }
 
    public List<IDRM> initPlugins(){
        List<IDRM> pluginlist = new ArrayList<>();
        
        Iterator<IDRM> iterator = getPlugins();
        
        if(!iterator.hasNext()){
            System.out.println("No plugins were found !");//
        }
        
        while(iterator.hasNext()){
            IDRM plugin = iterator.next();
            System.out.println("Initializing the plugin " + plugin.getName());
            plugin.init();
            
            pluginlist.add(plugin);
        }
        
        return pluginlist;
    }
    
}
