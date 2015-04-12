/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io.drm;

import java.util.Iterator;
import java.util.List;

/**
 * Define the methods needed to load and initialize the plugins. (In this case a plugin interface called Plugin.)
 * It is a code from James Selvakumar's tutorial on his blog : http://solitarygeek.com/java/a-simple-pluggable-java-application .
 * The goal was to do a plugin system.
 * @author Yves
 */
public interface PluginService {
        
    /**
     * Get all Plugin objects.
     * @return An iterator for plugins
     */
    Iterator<IDRM> getPlugins();
    
    /**
     * Add all Plugin objects.
     * @return A list of plugins
     */
    List<IDRM> initPlugins();
    
}
