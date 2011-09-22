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
import org.jlibrary.core.security.exception.GroupNotFoundException;
import org.jlibrary.test.AbstractRepositoryTest;

public class RemoveGroupTest extends AbstractRepositoryTest {

	public void testRemoveGroupWithoutPermission() {

		try {
			securityService.removeGroup(testTicket, testGroup.getId());			
			fail("The user shouldn´t have permissions to do this");
		} catch (SecurityException se) {
			assertEquals(se.getMessage(),SecurityException.NOT_ENOUGH_PERMISSIONS);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}
	
	public void testRemoveGroup() {

		try {
			securityService.removeGroup(adminTicket, testGroup.getId());
			// Check that it has been removed
			try {
				assertNull(securityService.findGroupById(testTicket, testGroup.getId()));
			} catch (GroupNotFoundException gnfe) {}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
	
	public void testRemoveGroupNotExists() {

		try {
			try {			
				securityService.removeGroup(adminTicket, testGroup.getId());
				fail("The group shouldn´t exist");
			} catch (GroupNotFoundException gnfe) {}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}			
	}	
}
