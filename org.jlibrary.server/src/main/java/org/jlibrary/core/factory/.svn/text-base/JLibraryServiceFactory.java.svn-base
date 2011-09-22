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
package org.jlibrary.core.factory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jlibrary.core.entities.ServerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JLibraryServiceFactory {

	static Logger logger = LoggerFactory.getLogger(JLibraryServiceFactory.class);

	private static final String FACTORY_PROPERTIES = "factory.properties";
	private static final String FACTORY_SERVICES = "factory.services";
	
	private static Map instances = new HashMap();
	
	private JLibraryServiceFactory() {}
	
	public static synchronized ServicesFactory getInstance(ServerProfile profile) {
		
		ServicesFactory factory = (ServicesFactory)instances.get(profile); 
		if (factory == null) {
			factory = loadServicesFactory(profile);
			instances.put(profile,factory);
		}
		return factory;
	}

	private static ServicesFactory loadServicesFactory(ServerProfile profile) {

		try {
			String factory;
			if (profile.getServicesFactory() != null) {
				factory = profile.getServicesFactory();
			} else {
				String propertiesFile = FACTORY_PROPERTIES;
				if (propertiesFile == null) {
					logger.error("Could not find factory properties file");
					return null;
				}
				InputStream is = null;
				Properties factoryProperties = new Properties();
				try {
					is = JLibraryServiceFactory.class.getClassLoader()
							.getResourceAsStream(propertiesFile);
					factoryProperties = new Properties();
					factoryProperties.load(is);
				} finally {
					if (is != null) {
						is.close();
					}
				}
				
			
				factory = factoryProperties.getProperty(FACTORY_SERVICES);
			}
			Class repositoryServiceClass = Class.forName(factory);			
			Constructor repositoryServiceConstructor = 
				repositoryServiceClass.getConstructor(new Class[]{ServerProfile.class});
			ServicesFactory servicesFactory = (ServicesFactory)
				repositoryServiceConstructor.newInstance(new Object[]{profile});

			return servicesFactory;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}	
}
