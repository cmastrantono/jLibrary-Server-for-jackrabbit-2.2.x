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
package org.jlibrary.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jlibrary.test.authors.CreateAuthorTest;
import org.jlibrary.test.authors.FindAuthorTest;
import org.jlibrary.test.authors.RemoveAuthorTest;
import org.jlibrary.test.bookmarks.CreateBookmarkTest;
import org.jlibrary.test.bookmarks.RemoveBookmarkTest;
import org.jlibrary.test.bookmarks.UpdateBookmarkTest;
import org.jlibrary.test.categories.AddDocumentTest;
import org.jlibrary.test.categories.CreateCategoryTest;
import org.jlibrary.test.categories.FindCategoriesAndDocumentsTest;
import org.jlibrary.test.categories.FindCategoryTest;
import org.jlibrary.test.categories.RemoveCategoryTest;
import org.jlibrary.test.categories.UpdateCategoryTest;
import org.jlibrary.test.content.BinaryContentTransferTest;
import org.jlibrary.test.content.CopyContentTest;
import org.jlibrary.test.content.CreateDirectoryTest;
import org.jlibrary.test.content.CreateDocumentTest;
import org.jlibrary.test.content.CreateResourceTest;
import org.jlibrary.test.content.FindDirectoryTest;
import org.jlibrary.test.content.FindDocumentTest;
import org.jlibrary.test.content.FindNodeTest;
import org.jlibrary.test.content.ManageResourcesTest;
import org.jlibrary.test.content.MoveContentTest;
import org.jlibrary.test.content.NodeChildrenTest;
import org.jlibrary.test.content.RemoveDirectoryTest;
import org.jlibrary.test.content.RemoveDocumentTest;
import org.jlibrary.test.content.RemoveResourceTest;
import org.jlibrary.test.content.UpdateDirectoryTest;
import org.jlibrary.test.content.UpdateDocumentTest;
import org.jlibrary.test.content.UpdateRepositoryTest;
import org.jlibrary.test.content.UpdateResourceTest;
import org.jlibrary.test.content.versions.CreateVersionTest;
import org.jlibrary.test.content.versions.FindVersionsTest;
import org.jlibrary.test.export.ExportRepositoryTest;
import org.jlibrary.test.export.ImportRepositoryTest;
import org.jlibrary.test.favorites.CreateFavoriteTest;
import org.jlibrary.test.favorites.DeleteFavoriteTest;
import org.jlibrary.test.locking.LockDocumentTest;
import org.jlibrary.test.locking.UnlockDocumentTest;
import org.jlibrary.test.notes.CreateNoteTest;
import org.jlibrary.test.notes.DeleteNoteTest;
import org.jlibrary.test.properties.CreateCustomPropertyTest;
import org.jlibrary.test.relations.AddRelationTest;
import org.jlibrary.test.relations.RemoveRelationTest;
import org.jlibrary.test.search.SearchTest;
import org.jlibrary.test.security.AddRestrictionTest;
import org.jlibrary.test.security.AddRoleTest;
import org.jlibrary.test.security.CreateGroupTest;
import org.jlibrary.test.security.CreateRoleTest;
import org.jlibrary.test.security.CreateUserTest;
import org.jlibrary.test.security.FindGroupTest;
import org.jlibrary.test.security.FindRoleTest;
import org.jlibrary.test.security.FindUserTest;
import org.jlibrary.test.security.RemoveGroupTest;
import org.jlibrary.test.security.RemoveRestrictionTest;
import org.jlibrary.test.security.RemoveRoleTest;
import org.jlibrary.test.security.RemoveUserTest;
import org.jlibrary.test.security.UpdateGroupTest;
import org.jlibrary.test.security.UpdateRoleTest;
import org.jlibrary.test.security.UpdateUserTest;

public class JLibraryTestSuite  {

    public static Test suite() { 
        
    	TestSuite suite = new TestSuite("jLibrary tests");
    	
    	// Login and repository setup
        suite.addTestSuite(CreateRepositoryTest.class);
        suite.addTestSuite(CreateUserTest.class);
        suite.addTestSuite(CreateRoleTest.class);
        suite.addTestSuite(CreateGroupTest.class);
        suite.addTestSuite(LoginTest.class);

        suite.addTestSuite(FindRepositoriesTest.class);

        // Security setup
        suite.addTestSuite(FindUserTest.class);
        suite.addTestSuite(FindGroupTest.class);
        suite.addTestSuite(FindRoleTest.class);
        suite.addTestSuite(AddRestrictionTest.class);
        suite.addTestSuite(AddRoleTest.class);
        suite.addTestSuite(UpdateUserTest.class);
        suite.addTestSuite(UpdateGroupTest.class);
        suite.addTestSuite(UpdateRoleTest.class);
        
        // Custom properties
        suite.addTestSuite(CreateCustomPropertyTest.class);
                
        // Content
        suite.addTestSuite(UpdateRepositoryTest.class);
        suite.addTestSuite(CreateDocumentTest.class);
        suite.addTestSuite(CreateDirectoryTest.class);
        suite.addTestSuite(CreateResourceTest.class);
        suite.addTestSuite(FindDocumentTest.class);
        suite.addTestSuite(FindNodeTest.class);
        suite.addTestSuite(FindDirectoryTest.class);
        suite.addTestSuite(NodeChildrenTest.class);
        suite.addTestSuite(UpdateDocumentTest.class);
        suite.addTestSuite(UpdateDirectoryTest.class);
        suite.addTestSuite(UpdateResourceTest.class);
        suite.addTestSuite(BinaryContentTransferTest.class);
        suite.addTestSuite(CopyContentTest.class);
        suite.addTestSuite(MoveContentTest.class);
        suite.addTestSuite(ManageResourcesTest.class);        
        
        // Locks
        suite.addTestSuite(LockDocumentTest.class);
        suite.addTestSuite(UnlockDocumentTest.class);        
        
        // Categories
        suite.addTestSuite(CreateCategoryTest.class);
        suite.addTestSuite(FindCategoryTest.class);
        suite.addTestSuite(UpdateCategoryTest.class);
        suite.addTestSuite(AddDocumentTest.class);
        suite.addTestSuite(FindCategoriesAndDocumentsTest.class);
        
        // Notes
        suite.addTestSuite(CreateNoteTest.class);
        suite.addTestSuite(DeleteNoteTest.class);
        
        // Relations
        suite.addTestSuite(AddRelationTest.class);
        
        // Authors
        suite.addTestSuite(CreateAuthorTest.class);
        suite.addTestSuite(FindAuthorTest.class);     
        
        // Bookmarks
        suite.addTestSuite(CreateBookmarkTest.class);
        suite.addTestSuite(UpdateBookmarkTest.class);        
        
        // Favorites        
        suite.addTestSuite(CreateFavoriteTest.class);
        suite.addTestSuite(DeleteFavoriteTest.class);
        
        // Versions
        suite.addTestSuite(FindVersionsTest.class);
        suite.addTestSuite(CreateVersionTest.class);
        
        // Search
        suite.addTestSuite(SearchTest.class); 
        
        // Export and Import
        suite.addTestSuite(ExportRepositoryTest.class);
        suite.addTestSuite(ImportRepositoryTest.class);
        
        // Cleanup
        suite.addTestSuite(RemoveDocumentTest.class);
        suite.addTestSuite(RemoveDirectoryTest.class);
        suite.addTestSuite(RemoveResourceTest.class);
        
        suite.addTestSuite(RemoveCategoryTest.class);

        suite.addTestSuite(RemoveRelationTest.class);
        
        suite.addTestSuite(RemoveAuthorTest.class); 

        suite.addTestSuite(RemoveBookmarkTest.class);                
        
        suite.addTestSuite(RemoveRoleTest.class);
        suite.addTestSuite(RemoveRestrictionTest.class);
        suite.addTestSuite(RemoveUserTest.class);
        suite.addTestSuite(RemoveGroupTest.class);
        suite.addTestSuite(RemoveRepositoryTest.class);
        //suite.addTestSuite(RemoveCustomPropertyTest.class);
        suite.addTestSuite(DisconnectTest.class);

        return suite; 
   }
}
