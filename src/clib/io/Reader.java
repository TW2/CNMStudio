/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clib.io;

import clib.layer.ImageLayer;
import clib.layer.PageLayer;
import clib.layer.ShapeLayer;
import clib.layer.TextLayer;
import clib.layer.vector.AbstractShape;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
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
    
    DualLock duallock = new DualLock();
    
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
                pageName, pageWidth, pageHeight,
                imageName, imageWidth, imageHeight, imageX, imageY,
                shapeName, shapeColor,
                textFamily, textStyle, textSize, textXOffset, textYOffset, textXMax, textYMax,
                isoLanguage;
	//flags nous indiquant la position du parseur
	private boolean inPages, inPage, inImage, inShapes, inShape, inTexts, inText, inISO, inContent;
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
                    textFamily = attributes.getValue(0);
                    textStyle = attributes.getValue(1);
                    textSize = attributes.getValue(2);
                    textXOffset = attributes.getValue(3);
                    textYOffset = attributes.getValue(4);
                    textXMax = attributes.getValue(5);
                    textYMax = attributes.getValue(6);
                    page.getTextsFrom(pl).add(tl);
                    inText = true;
                }else if(qName.equals("iso3166")){
                    isoLanguage = attributes.getValue(0);
                    inISO = true;
                }else if(qName.equals("content")){
                    inContent = true;
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
                    try {
                        il.setImage(duallock.decodeImage(buffer.toString()));
                    } catch (IOException ex) {
                        //Dommage pour nous xD;
                    }
                    buffer = null;
                    inImage = false;
                }else if(qName.equals("shapes")){
                    buffer = null;
                    inShapes = false;
                }else if(qName.equals("shape")){
                    sl.setName(shapeName);
                    sl.setGeneralPathColor(sl.getColorFromString(shapeColor));
                    for(AbstractShape as : sl.getShapesFromString(buffer.toString())){
                        sl.addShape(as);
                    }
                    buffer = null;
                    inShape = false;
                }else if(qName.equals("texts")){
                    buffer = null;
                    inTexts = false;
                }else if(qName.equals("text")){
                    if(textStyle.equalsIgnoreCase("plain")){
                        tl.setTextFont(new Font(textFamily, Font.PLAIN, Integer.parseInt(textSize)));
                    }else if(textStyle.equalsIgnoreCase("italic")){
                        tl.setTextFont(new Font(textFamily, Font.ITALIC, Integer.parseInt(textSize)));
                    }else if(textStyle.equalsIgnoreCase("bold")){
                        tl.setTextFont(new Font(textFamily, Font.BOLD, Integer.parseInt(textSize)));
                    }else if(textStyle.equalsIgnoreCase("bolditalic")){
                        tl.setTextFont(new Font(textFamily, Font.BOLD+Font.ITALIC, Integer.parseInt(textSize)));
                    }
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
                    tl.setText(iso.getISO_3166(isoLanguage), buffer.toString());
                    buffer = null;
                    inContent = false;
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
}
