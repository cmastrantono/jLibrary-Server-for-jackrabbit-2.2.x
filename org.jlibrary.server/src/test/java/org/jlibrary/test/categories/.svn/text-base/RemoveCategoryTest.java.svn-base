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

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.repository.exception.CategoryNotFoundException;

/**
 * Test to remove categories.
 * 
 * @author mpermar
 *
 */
public class RemoveCategoryTest extends AbstractCategoryTest {

	public void testRemoveChildCategory() {
		
		try {
			repositoryService.deleteCategory(testTicket, testChildCategory.getId());
			try {
				repositoryService.findCategoryById(testTicket, testChildCategory.getId());
				fail("The category " + testChildCategory.getId() + " shouldn´t exist");
			} catch (CategoryNotFoundException nfe) {}
			testCategory = repositoryService.findCategoryById(testTicket, testCategory.getId());
			assertEquals(testCategory.getCategories().size(), 0);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	

	public void testRemoveRootCategory() {
		
		try {
			repositoryService.deleteCategory(testTicket, testCategory.getId());
			try {
				repositoryService.findCategoryById(testTicket, testCategory.getId());
				fail("The category " + testCategory.getId() + " shouldn´t exist");
			} catch (CategoryNotFoundException nfe) {}

			Repository repository = 
				repositoryService.findRepository(repositoryName, testTicket);
			// 1 = Unknown
			assertEquals(repository.getCategories().size(),1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
