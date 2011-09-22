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

import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.test.AbstractRepositoryTest;

public class RemoveUserTest extends AbstractRepositoryTest {

	public void testRemoveUserWithoutPermission() {

		try {
			securityService.removeUser(testTicket, testUser.getId());			
			try {
				securityService.findUserByName(testTicket, testUserName);
				fail("The user shouldn´t have permissions to do this");
			} catch (UserNotFoundException unfe) {}
		} catch (SecurityException se) {
			assertEquals(se.getMessage(),SecurityException.NOT_ENOUGH_PERMISSIONS);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
	
	public void testRemoveUser() {

		try {
			securityService.removeUser(adminTicket, testUser.getId());
			// Check that it has been removed
			try {
				assertNull(securityService.findUserByName(testTicket, testUserName));
			} catch (UserNotFoundException unfe) {}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
	
	public void testRemoveUserNotExists() {

		try {
			try {			
				securityService.removeUser(adminTicket, testUser.getId());
				fail("The user shouldn´t exist");
			} catch (UserNotFoundException unfe) {}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
}
