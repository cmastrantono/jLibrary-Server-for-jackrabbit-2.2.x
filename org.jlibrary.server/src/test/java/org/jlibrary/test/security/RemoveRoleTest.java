/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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
import java.util.Iterator;

import org.jlibrary.core.entities.Rol;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.test.AbstractRepositoryTest;

/**
 * Test for removing a role from the user.
 * 
 * @author mpermar
 *
 */
public class RemoveRoleTest extends AbstractRepositoryTest {

	public void testRemoveRolFromUser() {
		
		try {
			Rol writerRole = null;
			Rol adminRole =  null;
			Collection roles = testUser.getRoles(repository.getId());
			Iterator it = roles.iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)) {
					writerRole = rol;
				}
				if (rol.getName().equals(Rol.ADMIN_ROLE_NAME)) {
					adminRole = rol;
				}
			}
			
			UserProperties userProperties = new UserProperties();
			userProperties.addProperty(UserProperties.USER_REPOSITORY, repository.getId());
			userProperties.addProperty(UserProperties.USER_ID, testUser.getId());
			userProperties.addProperty(UserProperties.USER_DELETE_ROL,writerRole);
			userProperties.addProperty(UserProperties.USER_DELETE_ROL,adminRole);
			testUser = securityService.updateUser(adminTicket, userProperties);

			roles = testUser.getRoles(repository.getId());
			boolean found = false;
			it = roles.iterator();
			while (it.hasNext()) {
				Rol rol = (Rol)it.next();
				if (rol.getName().equals(Rol.PUBLISHER_ROLE_NAME)) {
					found = true;
				}
			}
			assertFalse(found);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testRemoveRol() {
		
		try {
			securityService.removeRol(adminTicket, testRol.getId());
			Collection roles = securityService.findAllRoles(testTicket);
			assertTrue(roles.size() == 3);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
