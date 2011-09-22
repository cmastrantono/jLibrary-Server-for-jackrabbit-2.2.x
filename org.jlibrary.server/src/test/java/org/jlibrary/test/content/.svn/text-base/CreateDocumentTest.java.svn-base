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

import java.util.ArrayList;
import java.util.List;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to create documents.
 * 
 * @author mpermar
 *
 */
public class CreateDocumentTest extends AbstractContentTest {

	public void testCreateDocument() {
		
		Directory parent = repository.getRoot();
		DocumentProperties properties = MockHelper.createDocument(testTicket, parent.getId());
		
		try {
			testDocument = repositoryService.createDocument(testTicket, properties);
			assertNotNull(testDocument);
			assertNotNull(testDocument.getId());
			assertEquals(testDocument.getParent(),parent.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testCreateDocuments() {
		
		Directory parent = repository.getRoot();
		DocumentProperties properties1 = MockHelper.createDocument(testTicket, parent.getId());
		DocumentProperties properties2 = MockHelper.createDocument(testTicket, parent.getId());
		
		try {
			List propertiesList = new ArrayList();
			propertiesList.add(properties1);
			propertiesList.add(properties2);
			List documents = repositoryService.createDocuments(testTicket, propertiesList);
			
			assertNotNull(documents);
			assertEquals(documents.size(),2);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}	
	}
}
