/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cnmstudio;

import clib.io.drm.DRMException;
import clib.io.drm.DRMLess;
import clib.io.drm.IDRM;
import clib.io.drm.PluginServiceFactory;
import clib.io.drm.StandardPluginService;
import clib.layer.AudioLayer;
import clib.layer.FontLayer;
import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.layer.vector.AbstractShape;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Yves
 */
public class Reader {

    /*   Structure à lire :
     * <pages mode="(free or enc)">
     *      <page height="" width="" name="">
     *          <image name="" width="" height="" x="" y=""></image>
     *          <shapes>
     *              <shape name="" color=""></shape>
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
    
    PageHandler ph;
    
    public Reader(String path) throws ParserConfigurationException, SAXException, IOException{
        SAXParserFactory fabrique = SAXParserFactory.newInstance();
        SAXParser parseur = fabrique.newSAXParser();

        File fichier = new File(path);
        ph = new PageHandler();
        parseur.parse(fichier, ph);
    }
    
    public Page getPage(){
        return ph.getPage();
    }
    
    public class PageHandler extends DefaultHandler{
        
        // Storage of pages
	private Page page;
        
        //résultats de notre parsing
        private PageLayer pl;
        private ImageLayer il;
        private ShapeLayer sl;
        private TextLayer tl;
        
        private String
                allMode, allDRM,
                pageName, pageWidth, pageHeight,
                imageName, imageWidth, imageHeight, imageX, imageY,
                shapeName, shapeColor,
                textFamily, textStyle, textSize, textColor, textXOffset, textYOffset, textXMax, textYMax,
                isoLanguage,
                audioName;
	//flags nous indiquant la position du parseur
	private boolean inPages, inPage, inImage, inShapes, inShape, inTexts, inText, inISO, inContent, inAudio;
	//buffer nous permettant de récupérer les données 
	private StringBuffer buffer;
        
        public PageHandler(){
            super();
        }
        
        public Page getPage(){
            return page;
        }
        
        //détection d'ouverture de balise
        @Override
        public void startElement(String uri, String localName,
			String qName, Attributes attributes) throws SAXException{
            if(qName.equals("pages")){
                page = new Page();
                allMode = attributes.getValue(0);
                allDRM = attributes.getValue(1);                
                try{
                    usePlugin(allDRM);
                }catch (DRMException ex){
                    throw new SAXException(); //Force l'arrêt
                }               
                inPages = true;
            }else{
                buffer = new StringBuffer();
                if(qName.equals("page")){
                    pl = new PageLayer();
                    pageName = attributes.getValue(0);                    
                    pageWidth = attributes.getValue(1);
                    pageHeight = attributes.getValue(2);
                    page.getPages().add(pl);
                    inPage = true;
                }else if(qName.equals("image")){
                    il = new ImageLayer();
                    imageName = attributes.getValue(0);
                    imageWidth = attributes.getValue(1);
                    imageHeight = attributes.getValue(2);
                    imageX = attributes.getValue(3);
                    imageY = attributes.getValue(4);
                    page.getImagesFrom(pl).add(il);
                    inImage = true;
                }else if(qName.equals("shapes")){
                    inShapes = true;
                }else if(qName.equals("shape")){
                    sl = new ShapeLayer();
                    shapeName = attributes.getValue(0);
                    shapeColor = attributes.getValue(1);
                    page.getShapesFrom(pl).add(sl);
                    inShape = true;
                }else if(qName.equals("texts")){
                    inTexts = true;
                }else if(qName.equals("text")){
                    tl = new TextLayer();                    
                    textXOffset = attributes.getValue(0);
                    textYOffset = attributes.getValue(1);
                    textXMax = attributes.getValue(2);
                    textYMax = attributes.getValue(3);
                    page.getTextsFrom(pl).add(tl);
                    inText = true;
                }else if(qName.equals("iso3166")){
                    isoLanguage = attributes.getValue(0);
                    inISO = true;
                }else if(qName.equals("content")){
                    textFamily = attributes.getValue(0);
                    textStyle = attributes.getValue(1);
                    textSize = attributes.getValue(2);
                    textColor = attributes.getValue(3);
                    inContent = true;
                }else if(qName.equals("audio")){
                    audioName = attributes.getValue(0);
                    inAudio = true;
                }else{
                    //erreur, on peut lever une exception
//                        throw new SAXException("Balise "+qName+" inconnue.");
                }
            }
        }
        
        //détection fin de balise
        @Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException{
            if(qName.equals("pages")){
                inPages = false;
            }else{
                if(qName.equals("page")){
                    pl.setName(pageName);
                    pl.setWidth(Integer.parseInt(pageWidth));
                    pl.setHeight(Integer.parseInt(pageHeight));
                    buffer = null;
                    inPage = false;
                }else if(qName.equals("image")){
                    il.setName(imageName);
                    il.setWidth(Integer.parseInt(imageWidth));
                    il.setHeight(Integer.parseInt(imageHeight));
                    il.setXOffset(Integer.parseInt(imageX));
                    il.setYOffset(Integer.parseInt(imageY));
                    BufferedImage image = drmToUse.decryptImage(buffer.toString());
                    if(image!=null){
                        il.setImage(image);
                    }else{
                        throw new SAXException(); //Force l'arrêt
                    }
                    buffer = null;
                    inImage = false;
                }else if(qName.equals("shapes")){
                    buffer = null;
                    inShapes = false;
                }else if(qName.equals("shape")){
                    sl.setName(shapeName);
                    sl.setGeneralPathColor(sl.getColorFromString(shapeColor));
                    for(AbstractShape as : sl.getShapesFromString(drmToUse.decryptData(buffer.toString()))){
                        sl.addShape(as);
                    }
                    buffer = null;
                    inShape = false;
                }else if(qName.equals("texts")){
                    buffer = null;
                    inTexts = false;
                }else if(qName.equals("text")){                    
                    tl.setXOffset(Integer.parseInt(textXOffset));
                    tl.setYOffset(Integer.parseInt(textYOffset));
                    tl.setXMax(Integer.parseInt(textXMax));
                    tl.setYMax(Integer.parseInt(textYMax));
                    buffer = null;
                    inText = false;
                }else if(qName.equals("iso3166")){
                    buffer = null;
                    inISO = false;
                }else if(qName.equals("content")){
                    TextLayer.ISO_3166 iso = TextLayer.ISO_3166.United_States_of_America;
                    tl.setText(iso.getISO_3166(isoLanguage), drmToUse.decryptData(buffer.toString()));
                    buffer = null;
                    
                    Font font = new Font("Serif", Font.PLAIN, 12);
                    if(textStyle.equalsIgnoreCase("plain")){
                        font = new Font(textFamily, Font.PLAIN, Integer.parseInt(textSize));
                    }else if(textStyle.equalsIgnoreCase("italic")){
                        font = new Font(textFamily, Font.ITALIC, Integer.parseInt(textSize));
                    }else if(textStyle.equalsIgnoreCase("bold")){
                        font = new Font(textFamily, Font.BOLD, Integer.parseInt(textSize));
                    }else if(textStyle.equalsIgnoreCase("bolditalic")){
                        font = new Font(textFamily, Font.BOLD+Font.ITALIC, Integer.parseInt(textSize));
                    }                    
                    Color color = tl.getColorFromString(textColor);
                    tl.setDisplay(iso.getISO_3166(isoLanguage), FontLayer.create(font, color));
                    
                    inContent = false;
                }else if(qName.equals("audio")){
                    TextLayer.ISO_3166 iso = TextLayer.ISO_3166.United_States_of_America;
                    tl.setAudio(iso.getISO_3166(isoLanguage), AudioLayer.create(audioName, extractFolder + File.separator + drmToUse.decryptData(buffer.toString())));
                    buffer = null;
                    inAudio = false;
                }else{
                    //erreur, on peut lever une exception
//                        throw new SAXException("Balise "+qName+" inconnue.");
                }
            }
        }
        
        //détection de caractères
        @Override
	public void characters(char[] ch,int start, int length)
			throws SAXException{
            String lecture = new String(ch,start,length);
            if(buffer != null) {
                buffer.append(lecture);
            }       
	}
        
	//début du parsing
        @Override
	public void startDocument() throws SAXException {
//            System.out.println("Début du parsing");
	}
        
	//fin du parsing
        @Override
	public void endDocument() throws SAXException {
//            System.out.println("Fin du parsing");
//            System.out.println("Resultats du parsing");
//            for(ParticleObject p : lpo){
//                    System.out.println(p);
//            }
	}
        
    }
    
    //==========================================================================
    // CNM - ZIPPED ------------------------------------------------------------
    //==========================================================================
    
    private String extractFolder = "";
    
    public Reader(){
        loadPlugins();
    }
        
    public Page readCNM(String path, String extractfolder) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
        extractFolder = extractfolder;
        
        final int BUFFER = 2048;
        String xmlpath = null;
        
        BufferedOutputStream dest;
        FileInputStream fis = new FileInputStream(path);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis))) {
            ZipEntry entry;
            
            while((entry = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " +entry.getName());
                if(entry.getName().endsWith(".xml")){
                    xmlpath = extractfolder + File.separator + entry.getName();
                }
                
                int count;
                byte data[] = new byte[BUFFER];
                
                // write the files to the disk
                FileOutputStream fos = new FileOutputStream(extractfolder + File.separator + entry.getName());
                dest = new BufferedOutputStream(fos, BUFFER);
                
                while ((count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
            }
        }
        
        SAXParserFactory fabrique = SAXParserFactory.newInstance();
        SAXParser parseur = fabrique.newSAXParser();

        File fichier = new File(xmlpath);
        ph = new PageHandler();
        parseur.parse(fichier, ph);
        
        return ph.getPage();
    }
    
    //Plugins
    private List<IDRM> pluginList = new ArrayList<>();
    private IDRM drmToUse = new DRMLess();
    
    /**
     * Set plug-in to use (DRM to use)
     * @param name The name of this DRM
     */
    private boolean usePlugin(String name) throws DRMException{
        for(IDRM drm : pluginList){
            if(drm.getName().equalsIgnoreCase(name)){
                drmToUse = drm;
                return true;
            }
        }
        throw new DRMException();
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
