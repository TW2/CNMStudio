/*
 * Copyright (C) 2015 Yves
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

/**
 *
 * @author Yves
 */
public class Count {

    private int total = -1;
    private int elapsed = -1;
    
    public Count() {
        
    }
    
    public Count(int total) {
        this.total = total;
        elapsed = total;
    }
    
    public int getTotal(){
        return total;
    }
    
    public int getElapsed(){
        return elapsed;
    }
    
    public void decrementTimes(){
        if(elapsed > 0){
            elapsed -= 1;
        }        
    }
    
    public boolean hasNoMoreTime(){
        return elapsed == 0;
    }
    
    public boolean isInUse(){
        return total != -1 & elapsed != -1;
    }
    
    //==========================================================================
    // IMPORT / EXPORT
    //==========================================================================
    
    public String exportTotal(){
        return Integer.toString(total);
    }
    
    public String exportElapsed(){
        return Integer.toString(elapsed);
    }
    
    public void importTotal(String s){
        total = Integer.parseInt(s);
    }
    
    public void importElapsed(String s){
        elapsed = Integer.parseInt(s);
    }
}
