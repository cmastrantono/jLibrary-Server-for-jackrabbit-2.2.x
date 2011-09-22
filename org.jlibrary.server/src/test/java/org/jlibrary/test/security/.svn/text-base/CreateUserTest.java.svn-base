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
package org.jlibrary.test.security;

import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.test.AbstractRepositoryTest;

public class CreateUserTest extends AbstractRepositoryTest {

	public void testCreateUser() {

		System.out.println("Creating user");
		
		Ticket ticket = null;
		try {
			// Create the user in the new repository
			ticket = securityService.login(adminCredentials,repositoryName);			
			UserProperties userProperties = new UserProperties();
			userProperties.addProperty(UserProperties.USER_REPOSITORY, repository.getId());
			userProperties.addProperty(UserProperties.USER_NAME, testUserName);
			userProperties.addProperty(UserProperties.USER_PASSWORD, "test");
			userProperties.addProperty(UserProperties.USER_FIRSTNAME, "test");
			userProperties.addProperty(UserProperties.USER_LASTNAME, "test");
			userProperties.addProperty(UserProperties.USER_EMAIL, "test@test.com");
			testUser = securityService.createUser(ticket, userProperties);
			
			securityService.disconnect(ticket);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
}
