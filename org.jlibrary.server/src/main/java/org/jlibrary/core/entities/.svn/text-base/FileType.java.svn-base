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


/**
 * @author al
 *
 */
public final class FileType {

    private String extension;
    private String image;
    private String mimeType;
    private String extractor;
    
    public String getExtractor() {
		return extractor;
	}
	public void setExtractor(String extractor) {
		this.extractor = extractor;
	}
	public void setMimeType(String value){
        this.mimeType = value;
    }
    public String getMimeType(){
        return mimeType;
    }
    
    public void setImage(String value){
        this.image = value;
    }
    public String getImage(){
        return image;
    }
    
    public void setExtension(String value){
        this.extension = value;
    }
    public String getExtension(){
        return extension;
    }

    public String toString(){
        return "File type: \n" +
            "\tExtension: " + extension + "\n" +
            "\tImage: " + image + "\n" +
            "\tMime type: " + mimeType + "\n";
    }
}
