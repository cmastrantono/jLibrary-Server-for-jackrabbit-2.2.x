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

import org.jlibrary.core.entities.Credentials;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.entities.User;
import org.jlibrary.core.factory.JLibraryServiceFactory;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.SecurityService;
import org.jlibrary.core.security.exception.UserNotFoundException;

public class LoginTest extends AbstractRepositoryTest {	
	
	public void testSystemLogin() {

		Ticket ticket = null;
		try {
			ticket = securityService.login(adminCredentials,systemRepositoryName);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				securityService.disconnect(ticket);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testChangeAdminPassword() {

		String newAdminPassword = "newPassword";
		Ticket ticket = null;
		try {
			ticket = securityService.login(adminCredentials,systemRepositoryName);
			ticket.getUser().setPassword(newAdminPassword);
			UserProperties userProperties = 
				ticket.getUser().dumpProperties();
			securityService.updateUser(ticket,userProperties);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				securityService.disconnect(ticket);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
		
		try {
			ticket = securityService.login(adminCredentials,systemRepositoryName);
			fail("The password for the global admin should have changed");
		} catch (Exception e) {
			// Ok, exception received
		}
		
		// Try to login with normal password
		adminCredentials.setPassword(newAdminPassword);
		testSystemLogin();
		
		// Now replace back the default password
		try {			
			ticket = securityService.login(adminCredentials,systemRepositoryName);
			ticket.getUser().setPassword(User.DEFAULT_PASSWORD);
			UserProperties userProperties = 
				ticket.getUser().dumpProperties();
			securityService.updateUser(ticket,userProperties);
			adminCredentials.setPassword(User.DEFAULT_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				securityService.disconnect(ticket);
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public void testDefaultLoginFails() {

		Ticket ticket = null;
		try {
			ticket = securityService.login(adminCredentials,defaultRepositoryName);
			fail("The default workspace is not a jLibrary workspace");
		} catch (Exception e) {
			
		} finally {
			assertNull(ticket);
		}
	}
	
	public void testLoginRepositoryNotExist() {

		try {
			// Create the user in the new repository
			securityService.login(adminCredentials,"notexist");
			fail("The repository shouldn´t exist");
		} catch (RepositoryNotFoundException e) {
			// Correct
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}		
	
	public void testLogin() {

		try {			
			// Finally, perform the test
			testTicket = securityService.login(
					Credentials.credentialsForUser(testUser),repositoryName);
			adminTicket = securityService.login(adminCredentials,repositoryName);

			// Now, check failure
			User notExistingUser = new User();
			notExistingUser.setPassword("not exist");
			notExistingUser.setName("not exist");
			try {
				securityService.login(
						Credentials.credentialsForUser(notExistingUser),repositoryName);
				fail("The user shouldn´t exist");
			} catch (UserNotFoundException unfe) {
				// Correct
			}		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
}
