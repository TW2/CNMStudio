/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io.drm;

import java.io.File;
import java.io.IOException;

/**
 * A factory class to create PluginService objects.
 * It is a code from James Selvakumar's tutorial on his blog : http://solitarygeek.com/java/a-simple-pluggable-java-application .
 * The goal was to do a plugin system.
 * @author Yves
 */
public class PluginServiceFactory {
    
    /**
     * Create a StandardPluginService.
     * @param pluginsDirectory A directory that contains the plugins
     * @return A new StandardPluginService
     */
    public static StandardPluginService createPluginService(File pluginsDirectory){
        addPluginJarsToClasspath(pluginsDirectory);
        return StandardPluginService.getInstance();
    }
 
    /**
     * Add the plugin directory to classpath.
     */
    private static void addPluginJarsToClasspath(File pluginsDirectory){
        try{
            ClasspathUtils.addDirToClasspath(pluginsDirectory);
        } catch (IOException ex) {}
    }
}
