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
import org.jlibrary.test.util.MockHelper;

/**
 * Test to update documents.
 * 
 * @author mpermar
 *
 */
public class UpdateCategoryTest extends AbstractCategoryTest {

	public void testUpdate() {
	
		try {
			String newName = MockHelper.random(10, true, true);
			String newDescription = MockHelper.random(100,true,true);
			
			CategoryProperties properties = testCategory.dumpProperties();
			properties.setProperty(CategoryProperties.CATEGORY_NAME, newName);
			properties.setProperty(CategoryProperties.CATEGORY_DESCRIPTION, newDescription);
			testCategory = repositoryService.updateCategory(
					testTicket, testCategory.getId(), properties);
			
			String name = testCategory.getName();
			String description = testCategory.getDescription();
			
			assertNotNull(testCategory);
			assertEquals(name,newName);
			assertEquals(description,newDescription);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
}
