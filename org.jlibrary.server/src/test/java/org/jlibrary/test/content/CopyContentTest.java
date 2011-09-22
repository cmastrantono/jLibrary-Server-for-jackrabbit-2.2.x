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

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.ResourceNode;
import org.jlibrary.core.properties.DirectoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.ResourceNodeProperties;
import org.jlibrary.test.util.MockHelper;


/**
 * Test to copy data.
 * 
 * @author mpermar
 *
 */
public class CopyContentTest extends AbstractContentTest {

	public void testCopyDocument() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);
			
			DocumentProperties docProperties = 
				MockHelper.createDocument(testTicket, testDirectory.getId());
			Document document = repositoryService.createDocument(testTicket, docProperties);
			
			// Copy the document
			Document copiedDocument = (Document)
				repositoryService.copyNode(testTicket, document.getId(), destination.getId(), repository.getId());
			assertNotNull(copiedDocument);
			assertEquals(copiedDocument.getParent(),destination.getId());
			assertEquals(copiedDocument.getName(),document.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testCopyDirectory() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);
			
			DirectoryProperties childProperties = 
				MockHelper.createDirectory(testTicket, testDirectory.getId());
			Directory directory = repositoryService.createDirectory(testTicket, childProperties);
			
			// Copy the directory
			Directory copiedDirectory = (Directory)
				repositoryService.copyNode(testTicket, directory.getId(), destination.getId(), repository.getId());
			assertNotNull(copiedDirectory);
			assertEquals(copiedDirectory.getParent(),destination.getId());
			assertEquals(copiedDirectory.getName(),directory.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	public void testCopyResource() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);
			
			ResourceNodeProperties resProperties = 
				MockHelper.createResourceNode(testTicket, testDirectory.getId());
			ResourceNode resource = repositoryService.createResource(testTicket, resProperties);
			
			// Copy the document
			ResourceNode copiedResourceNode = (ResourceNode)
				repositoryService.copyNode(testTicket, resource.getId(), destination.getId(), repository.getId());
			assertNotNull(copiedResourceNode);
			assertEquals(copiedResourceNode.getParent(),destination.getId());
			assertEquals(copiedResourceNode.getName(),resource.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}		
}
