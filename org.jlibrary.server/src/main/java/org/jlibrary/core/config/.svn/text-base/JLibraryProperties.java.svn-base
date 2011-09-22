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
package org.jlibrary.core.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Properties holder class
 */
public class JLibraryProperties {

	static Logger logger = LoggerFactory.getLogger(JLibraryProperties.class);
	
	public static final String JLIBRARY_REPOSITORIES_HOME = "jlibrary.repositories.home";
	public static final String JLIBRARY_HOME = "jlibrary.home";
	
	private static Properties properties = null;
	
	public synchronized static String getProperty(String key) {
		
		if (properties == null) {
			init();
		}
		
		String home = System.getProperty("jlibrary.home");
		if (key == JLIBRARY_HOME) {
			if (home != null) {
				return home;
			} else {
				// Return current folder
				return new File(".").getAbsolutePath();
			}
		}
		
		if (key == JLIBRARY_REPOSITORIES_HOME) {
			String repoHome = properties.getProperty(key);
			if (repoHome == null) {
				// jlibrary properties overwrite any other setting
				if (home != null) {
					// -D variable overwrites jlibrary.properties
					return home + "/repositories";
				} else {
					String propHome = properties.getProperty(JLIBRARY_HOME);
					if (propHome != null) {
						// 
						return propHome + "/repositories";
					}
				}
			} else {
				return repoHome;
			}
		}
		
		return properties.getProperty(key);
		
	}

	private synchronized static void init() {
		
		properties = new Properties();
		InputStream is = null;
		try {
		    is = JLibraryProperties.class.getClassLoader().getResourceAsStream("jlibrary.properties");
			if (is == null) {
				// If null, that means that we are using -Djlibrary.home 
				if (System.getProperty("jlibrary.home") == null) {
					logger.error("jlibrary.properties file cannot be found and jlibrary.home startup variable hasn´t beeen defined");					
				}
			} else {
			    properties.load(is);
			}
		} catch (IOException e) {				
			logger.error(e.getMessage(),e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
	}
}