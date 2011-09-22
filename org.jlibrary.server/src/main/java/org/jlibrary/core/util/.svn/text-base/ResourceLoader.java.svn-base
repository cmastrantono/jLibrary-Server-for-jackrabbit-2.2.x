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
package org.jlibrary.core.util;

import java.net.URL;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Util class for resource access
 *
 * @author Martín Pérez Mariñán
 * @version 1.0
 */

public class ResourceLoader {

    private static ClassLoader loader;
    private static Icon iconError = ResourceLoader.getIcon(
				"org/canalejo/icons/error.gif");	
    /**
     * Singleton
     */
    private ResourceLoader() {}

    /**
     * Returns a resource URL
     *
     * @param resource Resource of which we wants to know its url
     * @return URL Resource's URL
     */
    public static URL getResourceAsURL(String resource) {

	if (loader == null) {
	    loader = ResourceLoader.class.getClassLoader();
	}
	return loader.getResource(resource);
    }

    /**
     * Returns a string with the location of the resource
     *
     * @param resource Resource of which we wants to know its location
     * @return String String with the resource's location
     */
    public static String getResource(String resource) {

        return getResourceAsURL(resource).toString();
    }

	
    /**
     * Returns an inputstream attached to the resource we want to obtain
     * 
	 * @param resource Resource that we want to obtain
	 * @return InputStream attached to the resource
	 */
	public static InputStream getResourceAsStream(String resource) {

        if (loader == null) {
            loader = ResourceLoader.class.getClassLoader();
        }
        return loader.getResourceAsStream(resource);
    }


	/**
	 * Returns an Icon. If the icon can't be obtained returns a default
	 * error icon
	 * 
	 * @param path Path of the icon we want to read
	 * @return Icon
	 */
	public static Icon getIcon(String path) {
		
		URL url = getResourceAsURL(path);
		if (url == null) {
			return iconError;
		} else {
			return new ImageIcon(url);
		}
	}

}