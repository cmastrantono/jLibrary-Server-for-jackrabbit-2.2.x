/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual 
* contributors as indicated by the @authors tag. See copyright.txt in the
* distribution for a full listing of individual contributors.
* All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.core.entities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * @author martin
 * @author al
 *
 * Class with different types for nodes
 */
public final class Types {

	static Logger logger = LoggerFactory.getLogger(Types.class);
	
	public static transient Integer FOLDER = new Integer(0);
	public static transient Integer HTML_DOCUMENT = new Integer(1);
	public static transient Integer TEXT_DOCUMENT = new Integer(2);
	public static transient Integer OO_DOCUMENT = new Integer(3);
	public static transient Integer XML_DOCUMENT = new Integer(4);	
	public static transient Integer WORD_DOCUMENT = new Integer(5);
	public static transient Integer EXCEL_DOCUMENT = new Integer(6);	
	public static transient Integer PDF_DOCUMENT = new Integer(7);	
	public static transient Integer IMAGE_DOCUMENT = new Integer(8);
	public static transient Integer POWERPOINT_DOCUMENT = new Integer(9);
        public static transient Integer AUTOCAD_DOCUMENT = new Integer(10);
	public static transient Integer OTHER = new Integer(-1);
        
        private transient FileTypes ft = new Types.FileTypes();
        
        private static transient Types instance = new Types();
        
	public static Integer getTypeForFile(String file) {
		
		if (file.toUpperCase().endsWith(".HTML") ||
			file.toUpperCase().endsWith(".WP") ||	
		    file.toUpperCase().endsWith(".HTM") ||
			file.toUpperCase().endsWith(".JSP") ||
			file.toUpperCase().endsWith(".ASP") ||
			file.toUpperCase().endsWith(".XHTML") ||
			file.toUpperCase().endsWith(".CFM") ||
			file.toUpperCase().endsWith(".CFML") ||
			file.toUpperCase().endsWith(".ASPX")) {
			return HTML_DOCUMENT;
		}

		if (file.toUpperCase().endsWith(".SXW") ||
			file.toUpperCase().endsWith(".SXI") ||
			file.toUpperCase().endsWith(".SXC") ||
			file.toUpperCase().endsWith(".ODT") ||
			file.toUpperCase().endsWith(".ODS") ||
			file.toUpperCase().endsWith(".ODF") ||
			file.toUpperCase().endsWith(".ODG") ||
			file.toUpperCase().endsWith(".ODP")
			)
		{
			return OO_DOCUMENT;
		}

		if (file.toUpperCase().endsWith(".PDF")) {
			return PDF_DOCUMENT;
		}

		if (file.toUpperCase().endsWith(".DOC") ||
			file.toUpperCase().endsWith(".RTF")) {
			return WORD_DOCUMENT;
		}		
		
		if (file.toUpperCase().endsWith(".XLS")) {
			return EXCEL_DOCUMENT;
		}
		
		if (file.toUpperCase().endsWith(".XML") ||
		    file.toUpperCase().endsWith(".XSL") ||
			file.toUpperCase().endsWith(".XSD")) {
			return XML_DOCUMENT;
		}
		
		if (file.toUpperCase().endsWith(".TXT") ||
			file.toUpperCase().endsWith(".JAVA") ||
			file.toUpperCase().endsWith(".CPP") ||
			file.toUpperCase().endsWith(".C") || 
			file.toUpperCase().endsWith(".BAT") ||
			file.toUpperCase().endsWith(".SH")) {
			return TEXT_DOCUMENT;
		}

		if (file.toUpperCase().endsWith(".JPG") ||
			    file.toUpperCase().endsWith(".PNG") ||
				file.toUpperCase().endsWith(".GIF") ||
				file.toUpperCase().endsWith(".BMP") ||
				file.toUpperCase().endsWith(".ICO") ||
				file.toUpperCase().endsWith(".TIF") ||
				file.toUpperCase().endsWith(".TIFF")) {
				
			return IMAGE_DOCUMENT;
		}
		
		if (file.toUpperCase().endsWith(".PPT") ||
			file.toUpperCase().endsWith(".PPS")) {
			return POWERPOINT_DOCUMENT;
		}
                
		if (file.toUpperCase().endsWith(".DWG")) {
			return AUTOCAD_DOCUMENT;
		}                
		
		return OTHER;
	}
	
	public static boolean isTextFile(Integer type) {
		
		if (type.equals(HTML_DOCUMENT) ||
			type.equals(XML_DOCUMENT) ||
			type.equals(TEXT_DOCUMENT)) {
			return true;
		}
		return false;
	}
	
	public static boolean isImageFile(Integer type) {
		
		if (type.equals(IMAGE_DOCUMENT)) {
			return true;
		}
		return false;
	}
	
	public static boolean isBrowsable(Integer type) {
		
		if (type.equals(HTML_DOCUMENT) ||
			type.equals(PDF_DOCUMENT) ||
			type.equals(WORD_DOCUMENT) ||
            type.equals(AUTOCAD_DOCUMENT) ||
			type.equals(EXCEL_DOCUMENT)) {
			
			return true;
		}
		return false;
	}
	
	public static boolean isResourceSensible(Integer type) {
		
		if (type.equals(HTML_DOCUMENT)) {
			
			return true;
		}
		return false;
	}
	
	public static boolean isIndexable(Integer type) {
		
		if (type.equals(IMAGE_DOCUMENT) ||
			type.equals(OTHER)) {
			
			return false;
		}
		return true;
	}
        
        public static String getMimeTypeForExtension(String ext){
            return instance.ft.getMime(ext);
        }
        public static String getExtractor(String ext){
            return instance.ft.getExtractor(ext);
        }        
        
    protected class FileTypes {

        private Map mimes;

        protected FileTypes(){  

        	InputStream is = null;
        	try {
	            XStream xstream = new XStream();
	            xstream.alias("file-types", ArrayList.class);
	            xstream.alias("file", FileType.class);
	            is = getClass().getClassLoader().getResourceAsStream("filetypes.xml");
	            BufferedReader br = new BufferedReader(new InputStreamReader(is));
	            List fileContent = (List)xstream.fromXML(br);
	            mimes = new HashMap();        
	            FileType mime;
	            for (Iterator i = fileContent.iterator(); i.hasNext();){
	                mime = (FileType)i.next();
	                mimes.put(mime.getExtension().toLowerCase(), mime);
	            }
        	} catch (Exception e) {
        		logger.error("[Error] Could not load filetypes.xml");
        	} finally {
        		if (is != null) {
        			try {
						is.close();
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
        		}
        	}
        }

        protected String getExtractor(String extension){

        	if (mimes == null) return null;
            Object o = mimes.get(extension);
            if (o == null){
                return null;
            }else{
                return ((FileType)o).getExtractor();
            }
        }
        
        protected String getMime(String extension){
        	
        	if (mimes == null) return "application/octet-stream";
        	
            Object o = mimes.get(extension);
            if (o == null){
                return "application/octet-stream";
            }else{
                return ((FileType)o).getMimeType();
            }
        }
    }
}
