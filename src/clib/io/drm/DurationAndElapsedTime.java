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
package clib.io.drm;

/**
 *
 * @author Antoine
 */
public class DurationAndElapsedTime implements Runnable {

    private IDRM drm;
    private ExternalInfo ei;
    private Thread elapsed = null;
    
    public DurationAndElapsedTime(IDRM drm, ExternalInfo ei) {
        this.drm = drm;
        this.ei = ei;
    }

    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
