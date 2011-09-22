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

import org.jlibrary.core.properties.GroupProperties;
import org.jlibrary.test.AbstractRepositoryTest;
import org.jlibrary.test.util.MockHelper;

public class UpdateGroupTest extends AbstractRepositoryTest {

	public void testUpdateGroup() {
		
		try {
			String newName = MockHelper.random(10, true, true);
			String newDescription = MockHelper.random(100,true,true);
			
			GroupProperties properties = testGroup.dumpProperties();
			properties.setProperty(GroupProperties.GROUP_ID, testGroup.getId());
			properties.setProperty(GroupProperties.GROUP_NAME, newName);
			properties.setProperty(GroupProperties.GROUP_DESCRIPTION, newDescription);
			properties.setProperty(GroupProperties.GROUP_REPOSITORY, testGroup.getRepository());
			testGroup = securityService.updateGroup(adminTicket, properties);
			
			String name = testGroup.getName();
			String description = testGroup.getDescription();
			
			assertNotNull(testRol);
			assertEquals(name,newName);
			assertEquals(description,newDescription);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
}
