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
package org.jlibrary.core.jcr.security.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.jcr.security.SynchronizerTemplate;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is a test synchronizer based on property files. You can use this 
 * synchronizer as an example for user/groups/roles synchronization from third 
 * party security systems. You could do exactly the same against an LDAP 
 * directory, against some database, etc. 
 * 
 * @author martin
 *
 */
public class PropertiesTestSynchronizer extends SynchronizerTemplate {

	static Logger logger = LoggerFactory.getLogger(PropertiesTestSynchronizer.class);
	
	public User[] getUsersToSynchronize() throws SecurityException {
		
		List users = new ArrayList();
		try {			
			Properties userProperties = new Properties();
			InputStream stream = null;
			try {
				stream = ResourceLoader
						.getResourceAsStream("org/jlibrary/core/jcr/security/test/users.xml");
				userProperties.loadFromXML(stream);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
			
			Iterator it = userProperties.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String value = (String)entry.getValue();
				String[] attributes = StringUtils.split(value,",");
				
				User user = new User();
				user.setName(attributes[0]);
				user.setFirstName(attributes[1]);
				user.setLastName(attributes[2]);
				user.setEmail(attributes[3]);
				user.setAdmin(Boolean.valueOf(attributes[4]).booleanValue());
				
				users.add(user);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return (User[])users.toArray(new User[]{});		
	}

	public Rol[] getRolesToSynchronize() throws SecurityException {
		
		// No extra roles defined
		return new Rol[]{};
	}

	public Group[] getGroupsToSynchronize() throws SecurityException {

		List groups = new ArrayList();
		try {			
			Properties userProperties = new Properties();
			InputStream stream = null;
			try {
				stream = ResourceLoader
						.getResourceAsStream("org/jlibrary/core/jcr/security/test/groups.xml");
				userProperties.loadFromXML(stream);
			} finally {
				if (stream != null) {
					stream.close();
				}
			}
			Iterator it = userProperties.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				String value = (String)entry.getValue();
				String[] attributes = StringUtils.split(value,",");
				
				Group group = new Group();
				group.setName(attributes[0]);
				group.setDescription(attributes[1]);
				
				groups.add(group);
			}			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		}
		return (Group[])groups.toArray(new Group[]{});		
	}

}
