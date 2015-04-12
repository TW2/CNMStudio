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
 *
 * To use your own private DRM you must contact me to have:
 * - a non-free agreement if you are a commercial group
 * - a free agreement if you can prove you are an association
 * Otherwise you must publish your private DRM on a public space of work in the Internet as GPLv3.
 * Contact me at assfxmaker@gmail.com
 */
package clib.io.drm;

import clib.io.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 * <p>DRMSample is just an example of DRM.<br />
 * To use your own private DRM you must contact me to have:
 * <ul><li>a non-free agreement if you are a commercial group</li>
 * <li>a free agreement if you can prove you are an association</li></ul>
 * Otherwise you must publish your private DRM on a public space of work in the Internet as GPLv3.<br />
 * Contact me at assfxmaker@gmail.com</p>
 * @author Antoine, Phil, Paul, Yves or TW2 (it's the same guy)
 */
public class DRMSample implements IDRM {

    public DRMSample(){
        
    }
    
    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String getName() {
        return "DRM example (Do not use this ! This is just an example.)";
    }

    @Override
    public String encryptData(String data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String decryptData(String data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getMedia() {
        return "Sample for DRM example";
    }

    @Override
    public Period getPeriod() {
        return new Period();
    }

    @Override
    public Duration getDuration() {
        Duration dur = Duration.createDurationForMinutes(10);
        return dur;
    }

    @Override
    public Count getCount() {
        return new Count();
    }

    @Override
    public boolean hasExternalFile() throws DRMException {
        return true;
    }

    @Override
    public String getExternalFileName() throws DRMException {
        return "00000.txt";
    }

    @Override
    public String encryptExternal(String data) throws DRMException {
        return data;
    }

    @Override
    public String decryptExternal(String data) throws DRMException {
        return data;
    }

    @Override
    public String encryptImage(BufferedImage data) {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(data, "png", baos);
            byte[] bytes = baos.toByteArray();
            String value = Base64.encodeBytes(bytes);
            return value; 
        }catch(Exception ex){
            return "failure";
        }        
    }

    @Override
    public BufferedImage decryptImage(String data) {
        try{
            byte[] bytes = Base64.decode(data);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedImage img = ImageIO.read(bais);
            return img;
        }catch(Exception ex){
            return null;
        }        
    }
    
    @Override
    public boolean useMAC() {
        return false;
    }

    @Override
    public boolean useFamilyName() {
        return false;
    }

    @Override
    public String getPassword() {
        return "";
    }
}
