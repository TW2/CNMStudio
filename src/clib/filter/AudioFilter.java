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
package clib.filter;

import java.io.File;

/**
 *
 * @author Antoine
 */
public class AudioFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        //Voir les dossiers dans le FileChooser
        if (f.isDirectory()) {
            return true;
        }

        //Voir les fichiers images dans le FileChooser
        if(f.getName().endsWith(".aac")
                | f.getName().endsWith(".m4a")
                | f.getName().endsWith(".mp3")){
            return true;
        }

        //Ne rien voir d'autres
        return false;
    }

    @Override
    public String getDescription() {
        //Montrer "Images" dans le sélecteur
        return "All MPEG-4 audio files + MP3 files";
    }
    
}
