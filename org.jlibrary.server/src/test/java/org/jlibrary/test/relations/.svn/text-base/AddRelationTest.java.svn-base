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
import org.jlibrary.test.util.MockHelper;

/**
 * Test to add relations to documents
 * 
 * @author mpermar
 *
 */
public class AddRelationTest extends AbstractRelationsTest {
	
	public void testAddBidirectionalRelation() {
		
		DocumentProperties documentProperties1 = 
			MockHelper.createDocument(testTicket,testTicket.getRepositoryId());
		DocumentProperties documentProperties2 = 
			MockHelper.createDocument(testTicket,testTicket.getRepositoryId());
		
		try {
			testDocument1 = repositoryService.createDocument(testTicket, documentProperties1);
			testDocument2 = repositoryService.createDocument(testTicket, documentProperties2);
			
			Relation relation = new Relation();
			relation.setBidirectional(true);
			relation.setDestinationNode(testDocument2);
			
			documentProperties1 = testDocument1.dumpProperties();
			documentProperties1.addProperty(DocumentProperties.DOCUMENT_ADD_RELATION, relation);
			Document updatedDocument = 
				repositoryService.updateDocument(testTicket, documentProperties1);
			Set relations = updatedDocument.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 1);
			Document relationDoc = (Document)relations.iterator().next();
			assertEquals(relationDoc.getId(),testDocument2.getId());
			
			Document updatedDocument2 = repositoryService.findDocument(testTicket, testDocument2.getId());
			relations = updatedDocument2.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 1);
			relationDoc = (Document)relations.iterator().next();
			
			assertTrue(relation.isBidirectional());
			assertEquals(relationDoc.getId(),testDocument1.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
	public void testAddUnidirectionalRelation() {
		
		DocumentProperties documentProperties3 = 
			MockHelper.createDocument(testTicket,testTicket.getRepositoryId());
		
		try {
			testDocument3 = repositoryService.createDocument(testTicket, documentProperties3);
			
			Relation relation = new Relation();
			relation.setBidirectional(false);
			relation.setDestinationNode(testDocument2);
			
			documentProperties3 = testDocument3.dumpProperties();
			documentProperties3.addProperty(DocumentProperties.DOCUMENT_ADD_RELATION, relation);
			Document updatedDocument = 
				repositoryService.updateDocument(testTicket, documentProperties3);
			Set relations = updatedDocument.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 1);
			
			Document relationDoc = (Document)relations.iterator().next();						
			assertFalse(relation.isBidirectional());
			assertEquals(relationDoc.getId(),testDocument2.getId());
			
			Document updatedDocument2 = repositoryService.findDocument(testTicket, testDocument2.getId());
			relations = updatedDocument2.getRelations();
			assertNotNull(relations);
			assertTrue(relations.size() == 1);
			relationDoc = (Document)relations.iterator().next();			
			assertEquals(relationDoc.getId(),testDocument1.getId());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}	
}
