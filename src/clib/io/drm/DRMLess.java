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

import clib.io.Base64;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Antoine
 */
public class DRMLess implements IDRM {

    public DRMLess() {
        
    }
    
    @Override
    public void init() {
        System.out.println("DRMLess is ready");
    }
    
    @Override
    public String getName() {
        return "DRMLess";
    }

    @Override
    public String encryptData(String data) {
        //No encrytion for this free model of DRM (which is not a DRM, lol).
        return data;
    }

    @Override
    public String decryptData(String data) {
        //No encrytion for this free model of DRM (which is not a DRM, lol).
        return data;
    }

    @Override
    public String getMedia() {
        //A small hack with a keyword. (Please type your media here, for example One Piece Chapter 777)
        return DRMType.Free.toString();
    }
    
    @Override
    public Period getPeriod() {
        return new Period();
    }

    @Override
    public Duration getDuration() {
        return new Duration();
    }

    @Override
    public Count getCount() {
        return new Count();
    }

    @Override
    public boolean hasExternalFile() throws DRMException {
        return false;
    }

    @Override
    public String getExternalFileName() throws DRMException {
        return null;
    }

    @Override
    public String encryptExternal(String data) throws DRMException {
        //No encrytion for this free model of DRM (which is not a DRM, lol).
        return data;
    }

    @Override
    public String decryptExternal(String data) throws DRMException {
        //No encrytion for this free model of DRM (which is not a DRM, lol).
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
