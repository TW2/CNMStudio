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
public class Duration {

    private long duration_timestamp = -1;
    private long elapsed_timestamp = -1;
    
    public Duration() {
        
    }
    
    public Duration(long duration_timestamp) {
        this.duration_timestamp = duration_timestamp;
        elapsed_timestamp = duration_timestamp;
    }
    
    public static Duration createDurationForDays(int numbersofdays){
        //Crée un timestamp en milliseconds
        long ts = numbersofdays * 24 * 3600000;
        Duration dur = new Duration();
        dur.duration_timestamp = ts;
        dur.elapsed_timestamp = ts;
        return dur;
    }
    
    public static Duration createDurationForHours(int numbersofhours){
        //Crée un timestamp en milliseconds
        long ts = numbersofhours * 3600000;
        Duration dur = new Duration();
        dur.duration_timestamp = ts;
        dur.elapsed_timestamp = ts;
        return dur;
    }
    
    public static Duration createDurationForMinutes(int numbersofminutes){
        //Crée un timestamp en milliseconds
        long ts = numbersofminutes * 60000;
        Duration dur = new Duration();
        dur.duration_timestamp = ts;
        dur.elapsed_timestamp = ts;
        return dur;
    }
    
    public static Duration createDurationForSeconds(int numbersofseconds){
        //Crée un timestamp en milliseconds
        long ts = numbersofseconds * 1000;
        Duration dur = new Duration();
        dur.duration_timestamp = ts;
        dur.elapsed_timestamp = ts;
        return dur;
    }
    
    public static Duration createDurationForMilliSeconds(int numbersofmillis){
        //Crée un timestamp en milliseconds
        Duration dur = new Duration();
        dur.duration_timestamp = numbersofmillis;
        dur.elapsed_timestamp = numbersofmillis;
        return dur;
    }
    
    public long getDuration(){
        return duration_timestamp;
    }
    
    public long getElapsedTime(){
        return elapsed_timestamp;
    }
    
    public void decrementMilliseconds(){
        if(elapsed_timestamp > 0){
            elapsed_timestamp -= 1;
        }        
    }
    
    public boolean hasNoMoreTime(){
        return elapsed_timestamp == 0;
    }
    
    public boolean isInUse(){
        return duration_timestamp != -1 & elapsed_timestamp != -1;
    }
    
    //==========================================================================
    // IMPORT / EXPORT
    //==========================================================================
    
    public String exportDuration(){
        return Long.toString(duration_timestamp);
    }
    
    public String exportElapsedTime(){
        return Long.toString(elapsed_timestamp);
    }
    
    public void importDuration(String s){
        duration_timestamp = Long.parseLong(s);
    }
    
    public void importElapsedTime(String s){
        elapsed_timestamp = Long.parseLong(s);
    }
}
