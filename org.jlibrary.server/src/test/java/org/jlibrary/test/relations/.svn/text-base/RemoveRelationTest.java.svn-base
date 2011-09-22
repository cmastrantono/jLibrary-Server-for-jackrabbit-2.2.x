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
package org.jlibrary.test.relations;

import java.util.Set;

import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Relation;
import org.jlibrary.core.properties.DocumentProperties;

/**
 * Test to remove relations from documents
 * 
 * @author mpermar
 *
 */
public class RemoveRelationTest extends AbstractRelationsTest {
	
	public void testRemoveBidirectionalRelation() {
		
		DocumentProperties documentProperties1 = testDocument1.dumpProperties();
		
		try {			
			Relation relation = new Relation();
			relation.setBidirectional(true);
			relation.setDestinationNode(testDocument2);
			
			documentProperties1.addProperty(DocumentProperties.DOCUMENT_DELETE_RELATION, relation);
			Document updatedDocument = 
				repositoryService.updateDocument(testTicket, documentProperties1);
			Set relations = updatedDocument.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 0);
			
			Document updatedDocument2 = repositoryService.findDocument(testTicket, testDocument2.getId());
			relations = updatedDocument2.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	public void testRemoveUnidirectionalRelation() {
		
		DocumentProperties documentProperties3 = testDocument3.dumpProperties();
		
		try {
			
			Relation relation = new Relation();
			relation.setBidirectional(false);
			relation.setDestinationNode(testDocument2);
			
			documentProperties3.addProperty(DocumentProperties.DOCUMENT_DELETE_RELATION, relation);
			Document updatedDocument = 
				repositoryService.updateDocument(testTicket, documentProperties3);
			Set relations = updatedDocument.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 0);
			
			Document updatedDocument2 = repositoryService.findDocument(testTicket, testDocument2.getId());
			relations = updatedDocument2.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
