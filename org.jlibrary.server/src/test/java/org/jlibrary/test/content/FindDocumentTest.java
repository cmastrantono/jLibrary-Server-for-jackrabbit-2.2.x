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
package org.jlibrary.test.content;

import java.util.Collection;

import org.jlibrary.core.entities.Document;

/**
 * Test to check finder methods.
 * 
 * @author mpermar
 *
 */
public class FindDocumentTest extends AbstractContentTest {

	public void testFindById() {
		
		try {
			Document document = 
				repositoryService.findDocument(testTicket, testDocument.getId());
			assertNotNull(document);
			assertEquals(document.getId(),testDocument.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testFindByName() {
		
		try {
			Collection documents = repositoryService.findDocumentsByName(testTicket, testDocument.getName());
			assertNotNull(documents);
			assertTrue(documents.size() == 1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
