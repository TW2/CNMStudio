/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.io.drm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Utility for plugins.
 * It is a code from James Selvakumar's tutorial on his blog : http://solitarygeek.com/java/a-simple-pluggable-java-application .
 * The goal was to do a plugin system.
 * @author Yves
 */
public class ClasspathUtils {
    
    // Parameters
    private static final Class[] parameters = new Class[] { URL.class };
 
    /**
     * Adds the jars in the given directory to classpath
     * @param directory
     * @throws IOException
     */
    public static void addDirToClasspath(File directory) throws IOException {
        if (directory.exists()){
            File[] files = directory.listFiles();
            for (File file : files) {
                addURL(file.toURI().toURL());
            }
        }
    }
 
    /**
     * Add URL to CLASSPATH
     * @param u URL
     * @throws IOException IOException
     */
    public static void addURL(URL u) throws IOException {
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        URL urls[] = sysLoader.getURLs();
        
        for (URL url : urls) {
            if (url.toString().equalsIgnoreCase(u.toString())) {
                //URL is already in the CLASSPATH
                return;
            }
        }
        
        Class sysclass = URLClassLoader.class;
        
        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[] { u });
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException t) {
            System.out.println("URL error : "+ t.getMessage());
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
    
}
