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

import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.test.properties.AbstractPropertyTest;

/**
 * Test to create documents.
 * 
 * @author mpermar
 *
 */
public class UpdateDocumentTest extends AbstractContentTest {

	private static String customPropertyName;

	@Override
	protected void setUp() throws Exception {
		
		super.setUp();
				
		customPropertyName = AbstractPropertyTest.customProperty.getName();		
	}
	
	public void testUpdateName() {
	
		try {
			testDocument = repositoryService.findDocument(testTicket, testDocument.getId());
			DocumentProperties properties = testDocument.dumpProperties();
			properties.setProperty(DocumentProperties.DOCUMENT_NAME, "changedName");
			testDocument = repositoryService.updateDocument(testTicket, properties);
			
			Object value = testDocument.getName();
			assertNotNull(value);
			assertEquals(value,"changedName");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	public void testAddCustomProperty() {

		try {
			testDocument = repositoryService.findDocument(testTicket, testDocument.getId());
			DocumentProperties properties = testDocument.dumpProperties();
			properties.addCustomProperty(customPropertyName, "testValue");
			testDocument = repositoryService.updateDocument(testTicket, properties);
			
			Object value = testDocument.getProperty(customPropertyName);
			assertNotNull(value);
			assertEquals(value,"testValue");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}	
}
