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
import java.util.Iterator;

import org.jlibrary.core.entities.Restriction;
import org.jlibrary.core.properties.UserProperties;
import org.jlibrary.test.AbstractRepositoryTest;

/**
 * Test restriction removal.
 * 
 * @author mpermar
 *
 */
public class RemoveRestrictionTest extends AbstractRepositoryTest {

	public void testAddRestriction() {
		
		try {
			UserProperties userProperties = new UserProperties();
			userProperties.addProperty(UserProperties.USER_REPOSITORY, repository.getId());
			userProperties.addProperty(UserProperties.USER_ID, testUser.getId());
			userProperties.addProperty(UserProperties.USER_DELETE_RESTRICTION,repository.getRoot().getId());
			securityService.updateUser(adminTicket, userProperties);

			Collection restrictions = 
				securityService.findAllRestrictions(testTicket, repository.getRoot().getId());
			boolean found = false;
			Iterator it = restrictions.iterator();
			while (it.hasNext()) {
				Restriction restriction = (Restriction)it.next();
				if (restriction.getMember().equals(testUser.getId())) {
					found = true;
				}
			}
			assertFalse(found);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
