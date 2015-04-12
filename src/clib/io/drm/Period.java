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

import java.util.Calendar;

/**
 *
 * @author Yves
 */
public class Period {

    private long start_timestamp = -1;
    private long end_timestamp = -1;
    
    public Period() {
        
    }
    
    public Period(long start_timestamp, long end_timestamp) {
        this.start_timestamp = start_timestamp;
        this.end_timestamp = end_timestamp;
    }
    
    public static Period createPeriod(Calendar calStart, Calendar calEnd){
        //Crée un timestamp en milliseconds
        Period p = new Period();
        p.start_timestamp = calStart.getTimeInMillis();
        p.end_timestamp = calEnd.getTimeInMillis();
        return p;
    }
        
    public long getStart(){
        return start_timestamp;
    }
    
    public long getEnd(){
        return end_timestamp;
    }
    
    public boolean hasPeriodStarted(){
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis()>=start_timestamp;
    }
    
    public boolean hasNoMoreTime(){
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis()>=end_timestamp;
    }
    
    public boolean isInUse(){
        return start_timestamp != -1 & end_timestamp != -1;
    }
    
    //==========================================================================
    // IMPORT / EXPORT
    //==========================================================================
    
    public String exportStart(){
        return Long.toString(start_timestamp);
    }
    
    public String exportEnd(){
        return Long.toString(end_timestamp);
    }
    
    public void importStart(String s){
        start_timestamp = Long.parseLong(s);
    }
    
    public void importEnd(String s){
        end_timestamp = Long.parseLong(s);
    }
}
