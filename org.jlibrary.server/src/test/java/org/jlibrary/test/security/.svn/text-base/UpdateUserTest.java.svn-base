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

import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.test.AbstractRepositoryTest;
import org.jlibrary.test.util.MockHelper;

public class UpdateUserTest extends AbstractRepositoryTest {

	public void testUpdateUser() {
		
		try {
			
			String newFirstname = MockHelper.random(50, true, true);
			String newLastname = MockHelper.random(50, true, true);
			String newPassword = MockHelper.random(10, true, true);
			String newEmail = MockHelper.random(10, true, true);
			
			UserProperties properties = testUser.dumpProperties();
			properties.setProperty(UserProperties.USER_ID, testUser.getId());
			properties.setProperty(UserProperties.USER_FIRSTNAME, newFirstname);
			properties.setProperty(UserProperties.USER_LASTNAME, newLastname);
			properties.setProperty(UserProperties.USER_PASSWORD, newPassword);
			properties.setProperty(UserProperties.USER_EMAIL, newEmail);
			testUser = securityService.updateUser(adminTicket, properties);
			
			String firstname = testUser.getFirstName();
			String lastname = testUser.getLastName();
			String password = testUser.getPassword();
			String email = testUser.getEmail();
			
			assertNotNull(testRol);
			assertEquals(email,newEmail);
			assertEquals(firstname, newFirstname);
			assertEquals(lastname, newLastname);
			assertEquals(password, newPassword);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}	
}
