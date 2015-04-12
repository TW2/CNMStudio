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

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Antoine
 */
public class FontLayer {
    
    Font font = new Font("Serif", Font.PLAIN, 12);
    Color color  = Color.black;
    
    public FontLayer(){
        
    }
    
    public static FontLayer create(Font f, Color c){
        FontLayer fl = new FontLayer();
        fl.font = f;
        fl.color = c;
        return fl;
    }
    
    public void setFont(Font f){
        font = f;
    }
    
    public Font getFont(){
        return font;
    }
    
    public void setColor(Color c){
        color = c;
    }
    
    public Color getColor(){
        return color;
    }
    
    @Override
    public String toString(){
        return font.getFamily();
    }
}
