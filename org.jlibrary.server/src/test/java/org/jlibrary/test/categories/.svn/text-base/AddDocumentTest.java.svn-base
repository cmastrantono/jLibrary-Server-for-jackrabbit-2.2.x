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
package org.jlibrary.test.categories;

import org.jlibrary.core.properties.CategoryProperties;
import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to add documents to categories.
 * 
 * @author mpermar
 *
 */
public class AddDocumentTest extends AbstractCategoryTest {
	
	public void testAddDocumentToCategory() {
		
		DocumentProperties documentProperties = 
			MockHelper.createDocument(testTicket,testTicket.getRepositoryId());
		
		try {
			testCategoryDocument = repositoryService.createDocument(testTicket, documentProperties);
			CategoryProperties categoryProperties = testCategory.dumpProperties();
			categoryProperties.addProperty(CategoryProperties.CATEGORY_ADD_NODE, testCategoryDocument.getId());
			
			testCategory = repositoryService.updateCategory(testTicket, testCategory.getId(), categoryProperties);
			
			assertNotNull(testCategory);
			assertNotNull(testCategory.getId());
			assertNull(testCategory.getParent());

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
