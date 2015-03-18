/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.filter;

import java.io.File;

/**
 *
 * @author Yves
 */
public class CNMFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File pathname) {
        //Voir les dossiers dans le FileChooser
        if (pathname.isDirectory()) {
            return true;
        }

        return pathname.getName().endsWith(".cnm");
    }

    @Override
    public String getDescription() {
        //Montrer "Images" dans le sélecteur
        return "CNM files";
    }
    
}
