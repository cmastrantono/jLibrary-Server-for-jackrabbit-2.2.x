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

import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.properties.RepositoryProperties;
import org.jlibrary.test.util.MockHelper;

/**
 * Test to update repositories.
 * 
 * @author mpermar
 *
 */
public class UpdateRepositoryTest extends AbstractContentTest {

	public void testUpdateRepository() {
		
		try {
			RepositoryProperties repositoryProperties = new RepositoryProperties();
			repositoryProperties.addProperty(
					RepositoryProperties.REPOSITORY_ID, repository.getId());
			String newName = MockHelper.random(10, true, true);
			String newDescription = MockHelper.random(100, true, true);

			repositoryProperties.addProperty(
					RepositoryProperties.REPOSITORY_NAME, newName);
			repositoryProperties.addProperty(
					RepositoryProperties.REPOSITORY_DESCRIPTION, newDescription);
			
			Repository updatedRepository = 
				repositoryService.updateRepository(adminTicket, repositoryProperties);

			
			assertNotNull(updatedRepository);
			assertEquals(updatedRepository.getName(),newName);
			assertEquals(updatedRepository.getDescription(),newDescription);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
}
