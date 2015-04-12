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

import java.awt.image.BufferedImage;

/**
 *
 * @author Antoine
 */
public interface IDRM {
    
    /**
     * Initialize the plug-in or confirm it.
     */
    public void init();
    
    /**
     * A short name for this DRM.
     * @return DRM name
     */
    public String getName();
    
    /**
     * Encrypt your data (which are texts, AAC filenames,...).
     * @param data The data to encrypt
     * @return Encrypted data
     */
    public String encryptData(String data);
    
    /**
     * Decrypt your data (which are texts, AAC filenames,...).
     * @param data The encrypted data
     * @return A readable text
     */
    public String decryptData(String data);
    
    /**
     * The name of the media concerned by the DRM.
     * Example: One Piece Chapter 777, Dragon Ball Chapter 123,...
     * @return Media name
     */
    public String getMedia();
    
    /**
     * The timestamp of the start time and end time can be used for this DRM. (optional)
     * @return Usage
     * @see ExternalInfo for more informations
     */
    public Period getPeriod();
    
    /**
     * The timestamp of the duration and elapsed time can be used for this DRM. (optional)
     * @return Usage
     * @see ExternalInfo for more informations
     */
    public Duration getDuration();
    
    /**
     * The count and total times the file protected by this DRM can be opened. (optional)
     * @return Usage
     * @see ExternalInfo for more informations
     */
    public Count getCount();
    
    /**
     * A boolean value that indicates if we have an external file
     * for the total times or for the duration. (optional)
     * @return true if an external counter exists otherwise false
     * @throws clib.io.drm.DRMException
     */
    public boolean hasExternalFile() throws DRMException;
    
    /**
     * <p>The filename of the external file if exists. (optional)<br />
     * Notice: You have to encrypt this external file.</p>
     * @return External counter filename
     * @throws clib.io.drm.DRMException
     */
    public String getExternalFileName() throws DRMException;
    
    /**
     * Encrypt your external data (counter for example and other data like name, MAC address,...).
     * @param data The data to encrypt
     * @return Encrypted data
     * @throws clib.io.drm.DRMException
     */
    public String encryptExternal(String data) throws DRMException;
    
    /**
     * Decrypt your external data (counter for example and other data like name, MAC address,...).
     * @param data The encrypted data
     * @return A readable text
     * @throws clib.io.drm.DRMException
     */
    public String decryptExternal(String data) throws DRMException;
    
    /**
     * Encrypt an image
     * @param data Data
     * @return Encrypted data
     */
    public String encryptImage(BufferedImage data);
    
    /**
     * Decrypt an image
     * @param data The encrypted data
     * @return Data
     */
    public BufferedImage decryptImage(String data);
    
    /**
     * MAC address can protect this DRM. (optional)
     * @return Usage
     * @see ExternalInfo for more informations
     */
    public boolean useMAC();
    
    /**
     * First name and last name can protect this DRM. (optional)
     * @return Usage
     * @see ExternalInfo for more informations
     */
    public boolean useFamilyName();
    
    /**
     * A password to verify before write an external file.
     * @return Password
     */
    public String getPassword();
}
