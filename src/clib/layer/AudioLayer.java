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

package clib.layer;

import java.io.File;

/**
 *
 * @author Yves
 */
public class AudioLayer {
    
    private String name = "ID";
    private String aacpath = "";
    
    public AudioLayer(){
        
    }
    
    public static AudioLayer create(String name, String aacpath){
        AudioLayer al = new AudioLayer();
        al.name = name.isEmpty() ? "ID" : name;
        al.aacpath = aacpath;
        return al;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setAACPath(String aacpath){
        this.aacpath = aacpath;
    }
    
    public String getAACPath(){
        return aacpath;
    }
    
    public String getAACName(){
        File f = new File(aacpath);
        return f.getName();
    }
    
    @Override
    public String toString(){
        if(aacpath.isEmpty()){
            return "";
        }
        return name + " - " + new File(aacpath).getName();
    }
    
}
