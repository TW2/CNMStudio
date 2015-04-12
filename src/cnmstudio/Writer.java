/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cnmstudio;

import clib.io.DualLock;
import clib.io.drm.DRMLess;
import clib.io.drm.IDRM;
import clib.io.drm.PluginServiceFactory;
import clib.io.drm.StandardPluginService;
import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author Yves
 */
public class Writer {
    
    //==========================================================================
    // XML ---------------------------------------------------------------------
    //==========================================================================
    
    /*   Structure à écrire :
     * <pages mode="(free or enc)">
     *      <page name="">
     *          <image id=""></image>
     *          <shapes>
     *              <shape></shape>
     *              ...
     *          </shapes>
     *          <texts> 
     *              <text fontfamily="" style="" size="">
     *                  <iso3166 lang="(iso3)">
     *                      <content></content> 
     *                      <audio name=""></audio>
     *                  </iso3166>
     *                  ...
     *              </text>
     *              ...
     *          </texts>
     *      </page>
     *      ...
     * </pages> */
    
    // Storage of pages
    Page page = new Page();
    
    public Writer(){
        loadPlugins();
    }
    
    public class PagesSource extends org.xml.sax.InputSource{
        
        // Storage of pages
        Page page = new Page();

        public PagesSource(Page page){
            super();
            this.page = page;

        }
        
        public Page getPages(){
            return page;
        }
    }
    
    public class PagesReader implements org.xml.sax.XMLReader{

        private ContentHandler chandler;
        private final AttributesImpl attributes = new AttributesImpl();
        private final Map<String,Boolean> features = new HashMap<>();
        private final Map<String,Object> properties = new HashMap<>();
        private EntityResolver resolver;
        private DTDHandler dhandler;
        private ErrorHandler ehandler;
        
        @Override
        public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            return features.get(name);
        }

        @Override
        public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            try{
                features.put(name, value);
            }catch(Exception ex){
            }            
        }

        @Override
        public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            return properties.get(name);
        }

        @Override
        public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            try{
                properties.put(name, value);
            }catch(Exception ex){
            }  
        }

        @Override
        public void setEntityResolver(EntityResolver resolver) {
            this.resolver = resolver;
        }

        @Override
        public EntityResolver getEntityResolver() {
            return resolver;
        }

        @Override
        public void setDTDHandler(DTDHandler handler) {
            this.dhandler = handler;
        }

        @Override
        public DTDHandler getDTDHandler() {
            return dhandler;
        }

        @Override
        public void setContentHandler(ContentHandler handler) {
            this.chandler = handler;
        }

        @Override
        public ContentHandler getContentHandler() {
            return chandler;
        }

        @Override
        public void setErrorHandler(ErrorHandler handler) {
            this.ehandler = handler;
        }

        @Override
        public ErrorHandler getErrorHandler() {
            return ehandler;
        }

        @Override
        public void parse(InputSource input) throws IOException, SAXException {

            if(!(input instanceof PagesSource)){
                throw new SAXException("The object isn't a ParticleSource");
            }
            if(chandler == null){
                throw new SAXException("ContentHandler not defined");
            }

            PagesSource source = (PagesSource)input;
            Page page = source.getPages();

            // Main element - beginning
            chandler.startDocument();
            attributes.addAttribute("", "", "mode", "mode", page.getMode()==Page.Mode.Free ? "free" : "enc");
            attributes.addAttribute("", "", "drm", "drm", drmToUse.getName());
            chandler.startElement("", "pages", "pages", attributes);
            attributes.clear();

            // pages element
            for(PageLayer pl : page.getPages()){

                // page element - beginning
                attributes.addAttribute("", "", "name", "name", pl.getName());
                attributes.addAttribute("", "", "width", "width", Integer.toString(pl.getWidth()));
                attributes.addAttribute("", "", "height", "height", Integer.toString(pl.getHeight()));
                chandler.startElement("", "page", "page", attributes);
                attributes.clear();
                
                // image block
                ImageLayer imagelayer = page.getImagesFrom(pl).get(0);
                attributes.addAttribute("", "", "name", "name", imagelayer.getName());
                attributes.addAttribute("", "", "width", "width", Integer.toString(imagelayer.getWidth()));
                attributes.addAttribute("", "", "height", "height", Integer.toString(imagelayer.getHeight()));
                attributes.addAttribute("", "", "x", "x", Integer.toString(imagelayer.getXOffset()));
                attributes.addAttribute("", "", "y", "y", Integer.toString(imagelayer.getYOffset()));
                chandler.startElement("", "image", "image", attributes);
                char[] image = drmToUse.encryptImage(imagelayer.getImage()).toCharArray();
                chandler.characters(image,0,image.length);
                chandler.endElement("", "image", "image");
                attributes.clear();
                
                // shapes element - beginning
                chandler.startElement("", "shapes", "shapes", attributes);
                
                for(ShapeLayer sl : page.getShapesFrom(pl)){
                    
                    // shape block
                    attributes.addAttribute("", "", "name", "name", sl.getName());
                    attributes.addAttribute("", "", "color", "color", sl.getStringColor());
                    chandler.startElement("", "shape", "shape", attributes);
                    char[] shape = drmToUse.encryptData(sl.getStringCommands()).toCharArray();
                    chandler.characters(shape,0,shape.length);
                    chandler.endElement("", "shape", "shape");
                    attributes.clear();
                    
                }
                
                // shapes element - end
                chandler.endElement("", "shapes", "shapes");
                
                // texts element - beginning
                chandler.startElement("", "texts", "texts", attributes);
                
                for(TextLayer tl : page.getTextsFrom(pl)){
                    
                    // text element - beginning                    
                    attributes.addAttribute("", "", "xo", "xo", Integer.toString(tl.getXOffset()));
                    attributes.addAttribute("", "", "yo", "yo", Integer.toString(tl.getYOffset()));
                    attributes.addAttribute("", "", "xm", "xm", Integer.toString(tl.getXMax()));
                    attributes.addAttribute("", "", "ym", "ym", Integer.toString(tl.getYMax()));
                    //TODO Add fields
                    chandler.startElement("", "text", "text", attributes);
                    attributes.clear();
                    
                    for(TextLayer.ISO_3166 iso : tl.getTextCountries()){
                        
                        // text element - beginning
                        attributes.addAttribute("", "", "lang", "lang", iso.getAlpha3());
                        chandler.startElement("", "iso3166", "iso3166", attributes);
                        attributes.clear();
                        
                        // content block
                        attributes.addAttribute("", "", "fontfamily", "fontfamily", tl.getDisplay(iso).getFont().getFamily());
                        attributes.addAttribute("", "", "style", "style", tl.getStringFontStyle(iso));
                        attributes.addAttribute("", "", "size", "size", Integer.toString(tl.getDisplay(iso).getFont().getSize()));
                        attributes.addAttribute("", "", "color", "color", tl.getStringColor(iso));
                        chandler.startElement("", "content", "content", attributes);
                        char[] content = drmToUse.encryptData(tl.getText(iso)).toCharArray();
                        chandler.characters(content,0,content.length);
                        chandler.endElement("", "content", "content");
                        attributes.clear();
                        
                        if(tl.getAudios().containsKey(iso)){
                            
                            // audio block
                            attributes.addAttribute("", "", "name", "name", tl.getAudio(iso).getName());
                            chandler.startElement("", "audio", "audio", attributes);                            
                            char[] audio = drmToUse.encryptData(tl.getAudio(iso).getAACName()).toCharArray();
                            chandler.characters(audio,0,audio.length);
                            chandler.endElement("", "audio", "audio");
                            attributes.clear();
                            
                        }

                        // text element - end
                        chandler.endElement("", "iso3166", "iso3166");
                        
                    }

                    // text element - end
                    chandler.endElement("", "text", "text");
                    
                }
                
                // texts element - end
                chandler.endElement("", "texts", "texts");

                // page element - end
                chandler.endElement("", "page", "page");
            }

            // Main element - end
            chandler.endElement("", "pages", "pages");
            chandler.endDocument();

        }

        @Override
        public void parse(String systemId) throws IOException, SAXException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
    
    public boolean createXML(String path){
        org.xml.sax.XMLReader pread = new PagesReader();
        InputSource psource = new PagesSource(page);
        Source source = new SAXSource(pread, psource);

        File file = new File(path);
        Result resultat = new StreamResult(file);
        
        try {
            TransformerFactory fabrique = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = fabrique.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, resultat);
        } catch (TransformerConfigurationException ex) {
            return false;
        } catch (TransformerException ex) {
            return false;
        }
        return true;
    }
    
    public void setPages(Page page){
        this.page = page;
    }
    
    //==========================================================================
    // CNM - ZIPPED ------------------------------------------------------------
    //==========================================================================
    
    public void createCNM(String path) throws TransformerConfigurationException,
            TransformerException, FileNotFoundException, IOException{
        
        // Prépare le fichier d'archive
        File archive = new File(path);
        
        // Prépare le fichier temporaire
        File xmlfile = new File(archive.getParentFile(), "temp.xml");
        
        // Crée la source XML dans le fichier temporaire
        org.xml.sax.XMLReader pread = new PagesReader();
        InputSource psource = new PagesSource(page);
        Source source = new SAXSource(pread, psource);
        
        Result resultat = new StreamResult(xmlfile);
        
        TransformerFactory fabrique = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = fabrique.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, resultat);
        
        // Compresse les fichiers dans l'archive
        FileInputStream fis; BufferedInputStream origin; ZipEntry entry;
        final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        
        FileOutputStream dest = new FileOutputStream(archive);
        try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
            
            fis = new FileInputStream(xmlfile);
            origin = new BufferedInputStream(fis, BUFFER);                      //Ouvre un flux pour le fichier à compresser
            entry = new ZipEntry(xmlfile.getName());                            //Crée l'entrée
            out.putNextEntry(entry);                                            //Déclare l'entrée à la sortie zip
            int count;
            while((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);                                      //Ecrit l'entrée dans la sortie zip
            }
            origin.close();                                                     //Ferme le flux de compression du fichier
            
            for(PageLayer pl : page.getPages()){
                for(TextLayer tl : page.getTextsFrom(pl)){
                    for(TextLayer.ISO_3166 iso : tl.getTextCountries()){
                        if(tl.getAudios().containsKey(iso)){                            
                            fis = new FileInputStream(tl.getAudio(iso).getAACPath());
                            origin = new BufferedInputStream(fis, BUFFER);
                            entry = new ZipEntry(tl.getAudio(iso).getAACName());
                            out.putNextEntry(entry);
                            while((count = origin.read(data, 0, BUFFER)) != -1) {
                                out.write(data, 0, count);
                            }
                            origin.close();
                        }
                    }
                }
            }
            
            // La compression est finie
        }
        
        // Effacement du fichier xml
        xmlfile.delete();
    }    
    
    //Plugins
    private List<IDRM> pluginList = new ArrayList<>();
    private IDRM drmToUse = new DRMLess();
    
    /**
     * Set plug-in to use (DRM to use)
     * @param name The name of this DRM
     */
    public void usePlugin(String name){
        for(IDRM drm : pluginList){
            if(drm.getName().equalsIgnoreCase(name)){
                drmToUse = drm;
            }
        }
    }
    
    /**
     * Load all plug-ins (load all DRMs)
     */
    private void loadPlugins(){
        File f = new File(getApplicationDirectory()+File.separator+"plugins");
        if(f.exists()){
            StandardPluginService pluginService = PluginServiceFactory.createPluginService(f);
            pluginList = pluginService.initPlugins();
        }
    }
    
    private String getApplicationDirectory(){
        if(System.getProperty("os.name").equalsIgnoreCase("Mac OS X")){
            java.io.File file = new java.io.File("");
            return file.getAbsolutePath();
        }
        String path = System.getProperty("user.dir");
        if(path.toLowerCase().contains("jre")){
            File f = new File(getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toString()
                    .substring(6));
            path = f.getParent();
        }
        return path;
    }
}
