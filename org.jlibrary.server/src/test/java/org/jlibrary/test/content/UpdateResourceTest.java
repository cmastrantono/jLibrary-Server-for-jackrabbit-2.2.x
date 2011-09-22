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
package org.jlibrary.test.content;

import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to update resources.
 * 
 * @author mpermar
 *
 */
public class UpdateResourceTest extends AbstractContentTest {

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
	}
	
	public void testUpdateName() {
	
		try {
			testResource = (ResourceNode)repositoryService.findNode(testTicket, testResource.getId());
			ResourceNodeProperties properties = testResource.dumpProperties();
			String newName = MockHelper.random(10, true, true);
			properties.setProperty(ResourceNodeProperties.RESOURCE_NAME, newName);
			testResource = repositoryService.updateResourceNode(testTicket, properties);
			
			Object value = testResource.getName();
			assertNotNull(value);
			assertEquals(value,newName);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}	
}
