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

import java.util.List;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Node;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to create resources.
 * 
 * @author mpermar
 *
 */
public class ManageResourcesTest extends AbstractContentTest {

	public void testAddResource() {
		
		Directory parent = repository.getRoot();
		ResourceNodeProperties properties = MockHelper.createResourceNode(testTicket, parent.getId());
		DocumentProperties docProperties = MockHelper.createDocument(testTicket, parent.getId());
		try {
			testResource = repositoryService.createResource(testTicket, properties);
			testDocument = repositoryService.createDocument(testTicket, docProperties);
			
			repositoryService.addResourceToDocument(testTicket, testResource.getId(), testDocument.getId());
			
			testDocument = repositoryService.findDocument(testTicket, testDocument.getId());
			assertNotNull(testDocument.getResourceNodes());
			assertTrue(testDocument.getResourceNodes().size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testFindResources() {
		
		try {
			List nodes = repositoryService.findNodesForResource(testTicket, testResource.getId());
			assertNotNull(nodes);
			assertTrue(nodes.size() == 1);
			Node node = (Node)nodes.iterator().next();
			assertEquals(testDocument.getId(),node.getId());			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testRemoveResource() {
		
		try {
			repositoryService.removeResourceNode(testTicket, testResource.getId(), testDocument.getId());
			
			testDocument = repositoryService.findDocument(testTicket, testDocument.getId());
			assertNotNull(testDocument.getResourceNodes());
			assertTrue(testDocument.getResourceNodes().size() == 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
