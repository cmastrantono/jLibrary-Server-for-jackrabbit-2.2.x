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

import java.util.Collection;
import java.util.List;

import org.jlibrary.core.security.exception.UserNotFoundException;
import org.jlibrary.test.AbstractRepositoryTest;

public class FindUserTest extends AbstractRepositoryTest {

	public void testFindUserById() {

		try {			
			assertNotNull(securityService.findUserById(testTicket, testUser.getId()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
	
	public void testFindUserByName() {

		try {
			// Create the user in the new repository
			assertNotNull(securityService.findUserByName(testTicket, testUser.getName()));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
	
	public void testFindNotExistingUser() {

		try {
			try {
				assertNull(securityService.findUserByName(testTicket, "notexist"));
			} catch (UserNotFoundException unfe) {}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
	
	public void testFindAllUsers() {

		try {
			Collection users = securityService.findAllUsers(testTicket);
			assertNotNull(users);
			assertTrue(users.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
}
