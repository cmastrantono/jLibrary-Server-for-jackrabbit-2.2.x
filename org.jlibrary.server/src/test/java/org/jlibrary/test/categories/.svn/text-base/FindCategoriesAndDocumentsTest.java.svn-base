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

import java.util.List;

import org.jlibrary.core.entities.Category;
import org.jlibrary.core.entities.Document;


/**
 * Test to check finder methods.
 * 
 * @author mpermar
 *
 */
public class FindCategoriesAndDocumentsTest extends AbstractCategoryTest {

	public void testFindCategoriesforDocument() {
		
		try {
			List nodes = 
				repositoryService.findCategoriesForNode(testTicket, testCategoryDocument.getId());
			assertNotNull(nodes);
			assertEquals(nodes.size(),1);
			Category category = (Category)nodes.get(0);
			assertEquals(testCategory.getId(),category.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testFindDocumentsForCategory() {
		
		try {
			List nodes = 
				repositoryService.findNodesForCategory(testTicket, testCategory.getId());
			assertNotNull(nodes);
			assertEquals(nodes.size(),1);
			Document document = (Document)nodes.get(0);
			assertEquals(testCategoryDocument.getId(),document.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
