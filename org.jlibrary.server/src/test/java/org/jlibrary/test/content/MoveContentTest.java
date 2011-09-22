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
 * Test to move data.
 * 
 * @author mpermar
 *
 */
public class MoveContentTest extends AbstractContentTest {

	public void testMoveDocument() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);

			DirectoryProperties dir2Properties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory source = 
				repositoryService.createDirectory(testTicket, dir2Properties);

			
			DocumentProperties docProperties = 
				MockHelper.createDocument(testTicket, source.getId());
			Document document = repositoryService.createDocument(testTicket, docProperties);
			
			// Copy the document
			Document movedDocument = (Document)
				repositoryService.moveNode(testTicket, document.getId(), destination.getId(), repository.getId());
			assertNotNull(movedDocument);
			assertEquals(movedDocument.getId(),document.getId());
			assertEquals(movedDocument.getParent(),destination.getId());
			assertEquals(movedDocument.getName(),document.getName());
			
			source = 
				repositoryService.findDirectory(testTicket, source.getId());
			assertEquals(source.getNodes().size(),0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testMoveDirectory() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);
			
			DirectoryProperties dir2Properties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory source = 
				repositoryService.createDirectory(testTicket, dir2Properties);
			
			DirectoryProperties childProperties = 
				MockHelper.createDirectory(testTicket, source.getId());
			Directory directory = repositoryService.createDirectory(testTicket, childProperties);
			
			// Copy the directory
			Directory movedDirectory = (Directory)
				repositoryService.moveNode(testTicket, directory.getId(), destination.getId(), repository.getId());
			assertNotNull(movedDirectory);
			assertEquals(movedDirectory.getId(),directory.getId());
			assertEquals(movedDirectory.getParent(),destination.getId());
			assertEquals(movedDirectory.getName(),directory.getName());
			
			source = 
				repositoryService.findDirectory(testTicket, source.getId());
			assertEquals(source.getNodes().size(),0);			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	public void testMoveResource() {

		Directory parent = repository.getRoot();
		try {
			DirectoryProperties dirProperties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory destination = 
				repositoryService.createDirectory(testTicket, dirProperties);
			
			DirectoryProperties dir2Properties = 
				MockHelper.createDirectory(testTicket, parent.getId());
			Directory source = 
				repositoryService.createDirectory(testTicket, dir2Properties);
			
			ResourceNodeProperties resProperties = 
				MockHelper.createResourceNode(testTicket, source.getId());
			ResourceNode resource = repositoryService.createResource(testTicket, resProperties);
			
			// Copy the document
			ResourceNode movedResourceNode = (ResourceNode)
				repositoryService.moveNode(testTicket, resource.getId(), destination.getId(), repository.getId());
			assertNotNull(movedResourceNode);
			assertEquals(movedResourceNode.getId(),resource.getId());
			assertEquals(movedResourceNode.getParent(),destination.getId());
			assertEquals(movedResourceNode.getName(),resource.getName());
			
			source = 
				repositoryService.findDirectory(testTicket, source.getId());
			assertEquals(source.getNodes().size(),0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}		
}
