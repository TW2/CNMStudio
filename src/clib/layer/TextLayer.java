/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clib.layer;

import clib.io.AudioFX;
import clib.layer.text.FromLimitPoint;
import clib.layer.text.ToLimitPoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Yves
 */
public class TextLayer {
    
    //private String text = "Essai est un essai avec pour objectif d'essayer à faire un essai.";
    private final FromLimitPoint flp = new FromLimitPoint();
    private final ToLimitPoint tlp = new ToLimitPoint();
    private boolean showBuilding = false;
    private ISO_3166 country = ISO_3166.United_States_of_America;
    private Map<ISO_3166,String> text = new HashMap<>();
    private Map<ISO_3166,FontLayer> display = new HashMap<>();
    private Map<ISO_3166,AudioLayer> audio = new HashMap<>();
    AudioFX afx = new AudioFX();
    
    public TextLayer(){
        
    }
    
    public void setText(ISO_3166 country, String s){
        text.put(country, s);
    }
    
    public void removeText(ISO_3166 country){
        text.remove(country);
    }
    
    public String getText(ISO_3166 country){
        if(getTextCountries().contains(country)){
            return text.get(country);
        }else{
            return " ";
        }
    }
    
    public void setDisplay(ISO_3166 country, FontLayer fl){
        display.put(country, fl);
    }
    
    public void removeDisplay(ISO_3166 country){
        display.remove(country);
    }
    
    public FontLayer getDisplay(ISO_3166 country){
        if(getDisplayCountries().contains(country)){
            return display.get(country);
        }else{
            return new FontLayer();
        }
    }
    
    public void setAudio(ISO_3166 country, AudioLayer al){
        audio.put(country, al);
    }
    
    public void removeAudio(ISO_3166 country){
        audio.remove(country);
    }
    
    public AudioLayer getAudio(ISO_3166 country){
        if(getAudioCountries().contains(country)){
            return audio.get(country);
        }else{
            return new AudioLayer();
        }
    }
    
    public Set<ISO_3166> getTextCountries(){
        return text.keySet();
    }
    
    public Set<ISO_3166> getDisplayCountries(){
        return display.keySet();
    }
    
    public Set<ISO_3166> getAudioCountries(){
        return audio.keySet();
    }
    
    public void setCountry(ISO_3166 country){
        this.country = country;
    }
    
    public ISO_3166 getCountry(){
        return country;
    }
    
    public void setTexts(Map<ISO_3166,String> text){
        this.text = text;
    }
    
    public void setDisplays(Map<ISO_3166,FontLayer> display){
        this.display = display;
    }
    
    public void setAudios(Map<ISO_3166,AudioLayer> audio){
        this.audio = audio;
    }
    
    public Map<ISO_3166,String> getTexts(){
        return text;
    }
    
    public Map<ISO_3166,FontLayer> getDisplays(){
        return display;
    }
    
    public Map<ISO_3166,AudioLayer> getAudios(){
        return audio;
    }
    
    public void playAudio(){
        afx.setListenPath(getAudio(country).getAACPath());
        afx.listenStart();
    }
    
    public void stopAudio(){
        afx.listenStop();
    }
    
    public void setXOffset(int x){
        flp.setX(x);
    }
    
    public int getXOffset(){
        return flp.getX();
    }
    
    public void setYOffset(int y){
        flp.setY(y);
    }
    
    public int getYOffset(){
        return flp.getY();
    }
    
    public void setXMax(int x){
        tlp.setX(x);
    }
    
    public int getXMax(){
        return tlp.getX();
    }
    
    public void setYMax(int y){
        tlp.setY(y);
    }
    
    public int getYMax(){
        return tlp.getY();
    }
    
    public void drawText(Graphics2D g){
        //Draw the text
        TextLayout layout;
        AffineTransform at = new AffineTransform();
        at.setToTranslation(flp.getX(), flp.getY());
        int lineBreak = tlp.getX()-flp.getX();
        LineBreakMeasurer lineMeasurer;
        int paragraphStart, paragraphEnd;
        //AttributedString as = new AttributedString(text.get(country));
        AttributedString as = new AttributedString(getText(country));
        as.addAttribute(TextAttribute.FONT, getDisplay(country).getFont());
        as.addAttribute(TextAttribute.FOREGROUND, getDisplay(country).getColor());
        as.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        as.addAttribute(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        as.addAttribute(TextAttribute.TRANSFORM, at);
        
        // Create a new LineBreakMeasurer from the paragraph.
        // It will be cached and re-used.        
        AttributedCharacterIterator paragraph = as.getIterator();
        paragraphStart = paragraph.getBeginIndex();
        paragraphEnd = paragraph.getEndIndex();
        FontRenderContext frc = g.getFontRenderContext();
        lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        
        // Set break width to width of Component.
        float breakWidth = (float)lineBreak;
        float drawPosY = flp.getY();
        // Set position to the index of the first character in the paragraph.
        lineMeasurer.setPosition(paragraphStart);
        
        // Emulation
        while (lineMeasurer.getPosition() < paragraphEnd) {
            layout = lineMeasurer.nextLayout(breakWidth);
            float drawPosX = layout.isLeftToRight() ? flp.getX() : flp.getX() + breakWidth - layout.getAdvance();
            // Move y-coordinate by the ascent of the layout.
            drawPosY += layout.getAscent();
            // Don't display text.....
            layout.draw(g, ((int)breakWidth-(int)layout.getBounds().getWidth())/2 + flp.getX(), drawPosY);
            // Move y-coordinate in preparation for next layout.
            drawPosY += layout.getDescent() + layout.getLeading();
        }        
//        g.drawString(text, flp.getX(), flp.getY());
    }
    
    public void drawRaw(Graphics2D g){        
        g.setColor(Color.green);
        g.fill(new Rectangle2D.Double(flp.getX()-5, flp.getY()-5, 10, 10));
        g.setColor(Color.red);
        g.fill(new Rectangle2D.Double(tlp.getX()-5, tlp.getY()-5, 10, 10));
        
        g.setColor(Color.cyan);
        g.fill(new Rectangle2D.Double(tlp.getX()-5, flp.getY()-5, 10, 10));
        g.fill(new Rectangle2D.Double(flp.getX()-5, tlp.getY()-5, 10, 10));
        
        Stroke stroke = g.getStroke();
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5, new float[]{5f,5f}, 0f));
        g.drawLine(flp.getX(), flp.getY(), tlp.getX(), flp.getY());
        g.drawLine(tlp.getX(), flp.getY(), tlp.getX(), tlp.getY());
        g.drawLine(tlp.getX(), tlp.getY(), flp.getX(), tlp.getY());
        g.drawLine(flp.getX(), tlp.getY(), flp.getX(), flp.getY());
        g.setStroke(stroke);
    }
    
    public void drawPlayLogo(Graphics2D g){
        g.setColor(Color.green);
        int[] xPoints = {
            flp.getX()+5,
            flp.getX()+15,
            flp.getX()+5
        };
        int[] yPoints = {
            flp.getY()-20,
            flp.getY()-15,
            flp.getY()-10
        };
        int nPoints = 3;
        if(getAudio(country).getAACPath().isEmpty()==false){
            g.fillPolygon(xPoints, yPoints, nPoints);
        }        
    }
    
    public void draw(Graphics2D g){        
        drawText(g);
        drawPlayLogo(g);
        if(showBuilding == true){
            drawRaw(g);
        }
    }
    
    public void showBuilding(boolean showBuilding){
        this.showBuilding = showBuilding;
    }
    
    public boolean existOnCoordinates(java.awt.Point p){
        if(getAudio(country).getAACPath().isEmpty()){
            return false;
        }
        java.awt.Rectangle area = new java.awt.Rectangle(flp.getX(), flp.getY(), tlp.getX()-flp.getX(), tlp.getY()-flp.getY());
        java.awt.Rectangle playicon = new java.awt.Rectangle(flp.getX()+5, flp.getY()-20, 10, 10);
        return area.contains(p) | playicon.contains(p);
    }
    
    // <editor-fold defaultstate="collapsed" desc=" ISO 3166 ">
    /** <p>A choice of countries.<br />Un choix de pays.</p> */
    public enum ISO_3166{
        Afghanistan("AF","AFG","Afghanistan"),
        Albania("AL","ALB","Albania"),
        Algeria("DZ","DZA","Algeria"),
        American_Samoa("AS","ASM","American Samoa"),
        Andorra("AD","AND","Andorra"),
        Angola("AO","AGO","Angola"),
        Anguilla("AI","AIA","Anguilla"),
        Antarctica("AQ","ATA","Antarctica"),
        Antigua_and_Barbuda("AG","ATG","Antigua and Barbuda"),
        Argentina("AR","ARG","Argentina"),
        Armenia("AM","ARM","Armenia"),
        Aruba("AW","ABW","Aruba"),
        Australia("AU","AUS","Australia"),
        Austria("AT","AUT","Austria"),
        Azerbaijan("AZ","AZE","Azerbaijan"),
        Bahamas("BS","BHS","Bahamas"),
        Bahrain("BH","BHR","Bahrain"),
        Bangladesh("BD","BGD","Bangladesh"),
        Barbados("BB","BRB","Barbados"),
        Belarus("BY","BLR","Belarus"),
        Belgium("BE","BEL","Belgium"),
        Belize("BZ","BLZ","Belize"),
        Benin("BJ","BEN","Benin"),
        Bermuda("BM","BMU","Bermuda"),
        Bhutan("BT","BTN","Bhutan"),
        Bolivia("BO","BOL","Bolivia"),
        Bosnia_and_Herzegovina("BA","BIH","Bosnia and Herzegovina"),
        Botswana("BW","BWA","Botswana"),
        Bouvet_Island("BV","BVT","Bouvet Island"),
        Brazil("BR","BRA","Brazil"),
        British_Indian_Ocean_Territory("IO","IOT","British Indian Ocean Territory"),
        British_Virgin_Islands("VG","VGB","British Virgin Islands"),
        Brunei_Darussalam("BN","BRN","Brunei Darussalam"),
        Bulgaria("BG","BGR","Bulgaria"),
        Burkina_Faso("BF","BFA","Burkina Faso"),
        Burundi("BI","BDI","Burundi"),
        Cambodia("KH","KHM","Cambodia"),
        Cameroon("CM","CMR","Cameroon"),
        Canada("CA","CAN","Canada"),
        Cape_Verde("CV","CPV","Cape Verde"),
        Cayman_Islands("KY", "CYM", "Cayman Islands"),
        Central_African_Republic("CF", "CAF", "Central African Republic"),
        Chad("TD", "TCD", "Chad"),
        Chile("CL", "CHL", "Chile"),
        China("CN", "CHN", "China"),
        Christmas_Island("CX", "CXR", "Christmas Island"),
        Cocos_Islands("CC", "CCK", "Cocos (Keeling), Islands"),
        Colombia("CO", "COL", "Colombia"),
        Comoros("KM", "COM", "Comoros"),
        Congo1("CD", "COD", "Congo"),
        Congo2("CG", "COG", "Congo"),
        Cook_Islands("CK", "COK", "Cook Islands"),
        Costa_Rica("CR", "CRI", "Costa Rica"),
        Cote_DIvoire("CI", "CIV", "Cote D'Ivoire"),
        Cuba("CU", "CUB", "Cuba"),
        Cyprus("CY", "CYP", "Cyprus"),
        Czech("CZ", "CZE", "Czech"),
        Denmark("DK", "DNK", "Denmark"),
        Djibouti("DJ", "DJI", "Djibouti"),
        Dominica("DM", "DMA", "Dominica"),
        Dominican("DO", "DOM", "Dominican"),
        Ecuador("EC", "ECU", "Ecuador"),
        Egypt("EG", "EGY", "Egypt"),
        El_Salvador("SV", "SLV", "El Salvador"),
        Equatorial_Guinea("GQ", "GNQ", "Equatorial Guinea"),
        Eritrea("ER", "ERI", "Eritrea"),
        Estonia("EE", "EST", "Estonia"),
        Ethiopia("ET", "ETH", "Ethiopia"),
        Faeroe_Islands("FO", "FRO", "Faeroe Islands"),
        Falkland_Islands("FK", "FLK", "Falkland Islands (Malvinas),"),
        Fiji("FJ", "FJI", "Fiji"),
        Finland("FI", "FIN", "Finland"),
        France("FR", "FRA", "France"),
        French_Guiana("GF", "GUF", "French Guiana"),
        French_Polynesia("PF", "PYF", "French Polynesia"),
        French_Southern_Territories("TF", "ATF", "French Southern Territories"),
        Gabon("GA", "GAB", "Gabon"),
        Gambia("GM", "GMB", "Gambia"),
        Georgia("GE", "GEO", "Georgia"),
        Germany("DE", "DEU", "Germany"),
        Ghana("GH", "GHA", "Ghana"),
        Gibraltar("GI", "GIB", "Gibraltar"),
        Greece("GR", "GRC", "Greece"),
        Greenland("GL", "GRL", "Greenland"),
        Grenada("GD", "GRD", "Grenada"),
        Guadaloupe("GP", "GLP", "Guadaloupe"),
        Guam("GU", "GUM", "Guam"),
        Guatemala("GT", "GTM", "Guatemala"),
        Guinea("GN", "GIN", "Guinea"),
        Guinea_Bissau("GW", "GNB", "Guinea-Bissau"),
        Guyana("GY", "GUY", "Guyana"),
        Haiti("HT", "HTI", "Haiti"),
        Heard_and_McDonald_Islands("HM", "HMD", "Heard and McDonald Islands"),
        Holy_See("VA", "VAT", "Holy See (Vatican City State),"),
        Honduras("HN", "HND", "Honduras"),
        Hong_Kong("HK", "HKG", "Hong Kong"),
        Hrvatska("HR", "HRV", "Hrvatska (Croatia),"),
        Hungary("HU", "HUN", "Hungary"),
        Iceland("IS", "ISL", "Iceland"),
        India("IN", "IND", "India"),
        Indonesia("ID", "IDN", "Indonesia"),
        Iran("IR", "IRN", "Iran"),
        Iraq("IQ", "IRQ", "Iraq"),
        Ireland("IE", "IRL", "Ireland"),
        Israel("IL", "ISR", "Israel"),
        Italy("IT", "ITA", "Italy"),
        Jamaica("JM", "JAM", "Jamaica"),
        Japan("JP", "JPN", "Japan"),
        Jordan("JO", "JOR", "Jordan"),
        Kazakhstan("KZ", "KAZ", "Kazakhstan"),
        Kenya("KE", "KEN", "Kenya"),
        Kiribati("KI", "KIR", "Kiribati"),
        Korea1("KP", "PRK", "Korea"),
        Korea2("KR", "KOR", "Korea"),
        Kuwait("KW", "KWT", "Kuwait"),
        Kyrgyz_Republic("KG", "KGZ", "Kyrgyz Republic"),
        Lao_Peoples_Democratic_Republic("LA", "LAO", "Lao People's Democratic Republic"),
        Latvia("LV", "LVA", "Latvia"),
        Lebanon("LB", "LBN", "Lebanon"),
        Lesotho("LS", "LSO", "Lesotho"),
        Liberia("LR", "LBR", "Liberia"),
        Libyan("LY", "LBY", "Libyan"),
        Liechtenstein("LI", "LIE", "Liechtenstein"),
        Lithuania("LT", "LTU", "Lithuania"),
        Luxembourg("LU", "LUX", "Luxembourg"),
        Macao("MO", "MAC", "Macao"),
        Macedonia("MK", "MKD", "Macedonia"),
        Madagascar("MG", "MDG", "Madagascar"),
        Malawi("MW", "MWI", "Malawi"),
        Malaysia("MY", "MYS", "Malaysia"),
        Maldives("MV", "MDV", "Maldives"),
        Mali("ML", "MLI", "Mali"),
        Malta("MT", "MLT", "Malta"),
        Marshall_Islands("MH", "MHL", "Marshall Islands"),
        Martinique("MQ", "MTQ", "Martinique"),
        Mauritania("MR", "MRT", "Mauritania"),
        Mauritius("MU", "MUS", "Mauritius"),
        Mayotte("YT", "MYT", "Mayotte"),
        Mexico("MX", "MEX", "Mexico"),
        Micronesia("FM", "FSM", "Micronesia"),
        Moldova("MD", "MDA", "Moldova"),
        Monaco("MC", "MCO", "Monaco"),
        Mongolia("MN", "MNG", "Mongolia"),
        Montserrat("MS", "MSR", "Montserrat"),
        Morocco("MA", "MAR", "Morocco"),
        Mozambique("MZ", "MOZ", "Mozambique"),
        Myanmar("MM", "MMR", "Myanmar"),
        Namibia("NA", "NAM", "Namibia"),
        Nauru("NR", "NRU", "Nauru"),
        Nepal("NP", "NPL", "Nepal"),
        Netherlands_Antilles("AN", "ANT", "Netherlands Antilles"),
        Netherlands("NL", "NLD", "Netherlands"),
        New_Caledonia("NC", "NCL", "New Caledonia"),
        New_Zealand("NZ", "NZL", "New Zealand"),
        Nicaragua("NI", "NIC", "Nicaragua"),
        Niger("NE", "NER", "Niger"),
        Nigeria("NG", "NGA", "Nigeria"),
        Niue("NU", "NIU", "Niue"),
        Norfolk_Island("NF", "NFK", "Norfolk Island"),
        Northern_Mariana_Islands("MP", "MNP", "Northern Mariana Islands"),
        Norway("NO", "NOR", "Norway"),
        Oman("OM", "OMN", "Oman"),
        Pakistan("PK", "PAK", "Pakistan"),
        Palau("PW", "PLW", "Palau"),
        Palestinian_Territory("PS", "PSE", "Palestinian Territory"),
        Panama("PA", "PAN", "Panama"),
        Papua_New_Guinea("PG", "PNG", "Papua New Guinea"),
        Paraguay("PY", "PRY", "Paraguay"),
        Peru("PE", "PER", "Peru"),
        Philippines("PH", "PHL", "Philippines"),
        Pitcairn_Island("PN", "PCN", "Pitcairn Island"),
        Poland("PL", "POL", "Poland"),
        Portugal("PT", "PRT", "Portugal"),
        Puerto_Rico("PR", "PRI", "Puerto Rico"),
        Qatar("QA", "QAT", "Qatar"),
        Reunion("RE", "REU", "Reunion"),
        Romania("RO", "ROU", "Romania"),
        Russian_Federation("RU", "RUS", "Russian Federation"),
        Rwanda("RW", "RWA", "Rwanda"),
        St__Helena("SH", "SHN", "St. Helena"),
        St__Kitts_and_Nevis("KN", "KNA", "St. Kitts and Nevis"),
        St__Lucia("LC", "LCA", "St. Lucia"),
        St__Pierre_and_Miquelon("PM", "SPM", "St. Pierre and Miquelon"),
        St__Vincent_and_the_Grenadines("VC", "VCT", "St. Vincent and the Grenadines"),
        Samoa("WS", "WSM", "Samoa"),
        San_Marino("SM", "SMR", "San Marino"),
        Sao_Tome_and_Principe("ST", "STP", "Sao Tome and Principe"),
        Saudi_Arabia("SA", "SAU", "Saudi Arabia"),
        Senegal("SN", "SEN", "Senegal"),
        Serbia_and_Montenegro("CS", "SCG", "Serbia and Montenegro"),
        Seychelles("SC", "SYC", "Seychelles"),
        Sierra_Leone("SL", "SLE", "Sierra Leone"),
        Singapore("SG", "SGP", "Singapore"),
        Slovakia("SK", "SVK", "Slovakia (Slovak Republic),"),
        Slovenia("SI", "SVN", "Slovenia"),
        Solomon_Islands("SB", "SLB", "Solomon Islands"),
        Somalia("SO", "SOM", "Somalia"),
        South_Africa("ZA", "ZAF", "South Africa"),
        South_Georgia_and_the_South_Sandwich_Islands("GS", "SGS", "South Georgia and the South Sandwich Islands"),
        Spain("ES", "ESP", "Spain"),
        Sri_Lanka("LK", "LKA", "Sri Lanka"),
        Sudan("SD", "SDN", "Sudan"),
        Suriname("SR", "SUR", "Suriname"),
        Svalbard___Jan_Mayen_Islands("SJ", "SJM", "Svalbard & Jan Mayen Islands"),
        Swaziland("SZ", "SWZ", "Swaziland"),
        Sweden("SE", "SWE", "Sweden"),
        Switzerland("CH", "CHE", "Switzerland"),
        Syrian_Arab_Republic("SY", "SYR", "Syrian Arab Republic"),
        Taiwan("TW", "TWN", "Taiwan"),
        Tajikistan("TJ", "TJK", "Tajikistan"),
        Tanzania("TZ", "TZA", "Tanzania"),
        Thailand("TH", "THA", "Thailand"),
        Timor_Leste("TL", "TLS", "Timor-Leste"),
        Togo("TG", "TGO", "Togo"),
        Tokelau("TK", "TKL", "Tokelau (Tokelau Islands),"),
        Tonga("TO", "TON", "Tonga"),
        Trinidad_and_Tobago("TT", "TTO", "Trinidad and Tobago"),
        Tunisia("TN", "TUN", "Tunisia"),
        Turkey("TR", "TUR", "Turkey"),
        Turkmenistan("TM", "TKM", "Turkmenistan"),
        Turks_and_Caicos_Islands("TC", "TCA", "Turks and Caicos Islands"),
        Tuvalu("TV", "TUV", "Tuvalu"),
        US_Virgin_Islands("VI", "VIR", "US Virgin Islands"),
        Uganda("UG", "UGA", "Uganda"),
        Ukraine("UA", "UKR", "Ukraine"),
        United_Arab_Emirates("AE", "ARE", "United Arab Emirates"),
        United_Kingdom_of_Great_Britain___N__Ireland("GB", "GBR", "United Kingdom of Great Britain & N. Ireland"),
        United_States_Minor_Outlying_Islands("UM", "UMI", "United States Minor Outlying Islands"),
        United_States_of_America("US", "USA", "United States of America"),
        Uruguay("UY", "URY", "Uruguay"),
        Uzbekistan("UZ", "UZB", "Uzbekistan"),
        Vanuatu("VU", "VUT", "Vanuatu"),
        Venezuela("VE", "VEN", "Venezuela"),
        Viet_Nam("VN", "VNM", "Viet Nam"),
        Wallis_and_Futuna_Islands("WF", "WLF", "Wallis and Futuna Islands"),
        Western_Sahara("EH", "ESH", "Western Sahara"),
        Yemen("YE", "YEM", "Yemen"),
        Zambia("ZM", "ZMB", "Zambia"),
        Zimbabwe("ZW", "ZWE", "Zimbabwe"),
        British_Antarctic_Territory("BQ", "ATB", "British Antarctic Territory"),
        Burma("BU", "BUR", "Burma"),
        Byelorussian("BY", "BYS", "Byelorussian"),
        Canton___Enderbury_Islands("CT", "CTE", "Canton & Enderbury Islands"),
        Czechoslovakia("CS", "CSK", "Czechoslovakia"),
        Dahomey("DY", "DHY", "Dahomey"),
        Dronning_Maud_Land("NQ", "ATN", "Dronning Maud Land"),
        East_Timor("TP", "TMP", "East Timor"),
        Ethiopia2("ET", "ETH", "Ethiopia"),
        France2("FX", "FXX", "France"),
        French_fars_and_Issas("AI", "AFI", "French fars and Issas"),
        French_Southern_and_Antarctic_Territories("FQ", "ATF", "French Southern and Antarctic Territories"),
        German_Democratic_Republic("DD", "DDR", "German Democratic Republic"),
        Germany2("DE", "DEU", "Germany"),
        Gilbert___Ellice_Islands("GE", "GEL", "Gilbert & Ellice Islands"),
        Johnston_Island("JT", "JTN", "Johnston Island"),
        Midway_Islands("MI", "MID", "Midway Islands"),
        Netherlands_Antilles2("AN", "ANT", "Netherlands Antilles"),
        Neutral_Zone("NT", "NTZ", "Neutral Zone"),
        New_Hebrides("NH", "NHB", "New Hebrides"),
        Pacific_Islands("PC", "PCI", "Pacific Islands"),
        Panama2("PA", "PAN", "Panama"),
        Panama_Canal_Zone("PZ", "PCZ", "Panama Canal Zone"),
        Romania2("RO", "ROM", "Romania"),
        St__Kitts_Nevis_Anguilla("KN", "KNA", "St. Kitts-Nevis-Anguilla"),
        Sikkim("SK", "SKM", "Sikkim"),
        Southern_Rhodesia("RH", "RHO", "Southern Rhodesia"),
        Spanish_Sahara("EH", "ESH", "Spanish Sahara"),
        US_Miscellaneous_Pacific_Islands("PU", "PUS", "US Miscellaneous Pacific Islands"),
        USSR("SU", "SUN", "USSR"),
        Upper_Volta("HV", "HVO", "Upper Volta"),
        Vatican_City_State("VA", "VAT", "Vatican City State (Holy See)"),
        Viet_Nam2("VD", "VDR", "Viet-Nam"),
        Wake_Island("WK", "WAK", "Wake Island"),
        Yemen1("YD", "YMD", "Yemen"),
        Yemen2("YE", "YEM", "Yemen"),
        Yugoslavia1("YU", "YUG", "Yugoslavia"),
        Yugoslavia2("YU", "YUG", "Yugoslavia"),
        Zaire("ZR", "ZAR", "Zaire"),
        Unknown("XX", "XXX", "Unknown");
        
        private final String alpha_2;
        private final String alpha_3;
        private final String name;

        ISO_3166(String alpha_2, String alpha_3, String name){
            this.alpha_2 = alpha_2;
            this.alpha_3 = alpha_3;
            this.name = name;
        }
        
        public String getAlpha2(){
            return alpha_2;
        }
        
        public String getAlpha3(){
            return alpha_3;
        }
        
        public String getCountry(){
            return name;
        }
        
        /** Find a value of ISO_3166 by searching for the alpha 2 code or 
         * the alpha 3 code or the name of the country. The name of the 
         * countries are in English only.
         * @param search
         * @return  */
        public ISO_3166 getISO_3166(String search){
            ISO_3166 iso = ISO_3166.Unknown;
            for(ISO_3166 x : ISO_3166.values()){
                if(search.equalsIgnoreCase(x.getAlpha2())){
                    iso = x;
                }
                if(search.equalsIgnoreCase(x.getAlpha3())){
                    iso = x;
                }
                if(search.equalsIgnoreCase(x.getCountry())){
                    iso = x;
                }
            }
            return iso;
        }
        
        @Override
        public String toString(){
            return getCountry() + ", " + getAlpha3() + ", " + getAlpha2();
        }
    }
    // </editor-fold>
    
    //==========================================================================
    //========== EXPORT
    //==========================================================================
    
    public String getStringFontStyle(ISO_3166 country){
        Font font = getDisplay(country).getFont();
        if(font.isBold() && font.isItalic()){
            return "BoldItalic";
        }else if(font.isBold()){
            return "Bold";
        }else if(font.isItalic()){
            return "Italic";
        }else{
            return "Plain";
        }
    }
    
    public String getStringColor(ISO_3166 country){
        Color color = getDisplay(country).getColor();
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        String red = Integer.toHexString(r).length()<2 ? "0"+Integer.toHexString(r) : Integer.toHexString(r);
        String green = Integer.toHexString(g).length()<2 ? "0"+Integer.toHexString(g) : Integer.toHexString(g);
        String blue = Integer.toHexString(b).length()<2 ? "0"+Integer.toHexString(b) : Integer.toHexString(b);
        return red+green+blue;
    }
    
    //==========================================================================
    //========== IMPORT
    //==========================================================================
    
    public Color getColorFromString(String s){
        String red = s.substring(0, 2);
        String green = s.substring(2, 4);
        String blue = s.substring(4);
        int r = Integer.parseInt(red, 16);
        int g = Integer.parseInt(green, 16);
        int b = Integer.parseInt(blue, 16);        
        return new Color(r, g, b);
    }
    
}
