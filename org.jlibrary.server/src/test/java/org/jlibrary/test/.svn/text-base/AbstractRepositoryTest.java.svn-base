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
package org.jlibrary.test;

import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.lang.math.RandomUtils;
import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Group;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.entities.ServerProfile;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.factory.ServicesFactory;
import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.test.profiles.ProfileTestFactory;

public abstract class AbstractRepositoryTest extends TestCase {

	protected static String repositoryName = "";
	protected static String testUserName="";
	protected static String testGroupName="";
	
	protected static String systemRepositoryName = "system";
	protected static String defaultRepositoryName = "default";
	
	protected static Ticket testTicket;
	protected static Ticket adminTicket;
	protected static Credentials adminCredentials;
	protected static SecurityService securityService;
	protected static RepositoryService repositoryService;
	protected static SearchService searchService;
	protected static Repository repository;
	protected static User testUser;
	protected static Group testGroup;
	protected static Rol testRol;
	
	protected static Document testDocument;
	protected static Category testCategory;
	
	private static Boolean initialized = Boolean.FALSE;
	private String serverProfileFactoryName;
	
	@Override
	protected void setUp() throws Exception {

		super.setUp();
	
		synchronized(initialized) {
			if (!initialized) {
				initialized = Boolean.TRUE;
				InputStream is = getClass().getClassLoader().getResourceAsStream("test.properties");
				is = getClass().getClassLoader().getResourceAsStream("test.properties");
				Properties properties = new Properties();
				properties.load(is);
				
				repositoryName = (String)properties.get("test.repository.name") + RandomUtils.nextInt();
				testUserName = (String)properties.get("test.user.name");
				testGroupName = (String)properties.get("test.group.name");
				serverProfileFactoryName = (String)properties.get("test.server.profile.factory");

				System.out.println("repositoryName: " + repositoryName);
				System.out.println("testUserName: " + testUserName);
				System.out.println("testGroupName: " + testGroupName);
				System.out.println("serverProfileFactoryName: " + serverProfileFactoryName);
				adminCredentials = new Credentials();
				adminCredentials.setUser(User.ADMIN_NAME);
				adminCredentials.setPassword(User.DEFAULT_PASSWORD);
				
				Class serverProfileFactoryClass = Class.forName(serverProfileFactoryName);				
				ProfileTestFactory profileFactory = 
					(ProfileTestFactory)serverProfileFactoryClass.newInstance();
				ServerProfile profile = profileFactory.getServerProfile();
				
				ServicesFactory factory = JLibraryServiceFactory.getInstance(profile);
				securityService = factory.getSecurityService();
				repositoryService = factory.getRepositoryService();
				searchService = factory.getSearchService();
				
				is.close();
			}
		}
	}
}
