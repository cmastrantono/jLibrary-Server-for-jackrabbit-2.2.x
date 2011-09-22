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
package org.jlibrary.core.search;

import java.util.Collection;

import org.jlibrary.core.entities.Ticket;

/**
 * @author martin
 *
 * Search service interface
 */
public interface SearchService {

	// Query types
	public static final String SEARCH_CONTENT = "contents";
	public static final String SEARCH_KEYWORDS = "keywords";
	public static final String SEARCH_DESCRIPTION = "description";
	public static final String SEARCH_NOTES = "notes";
											 						 
	/**
	 * Performs a search on the given repository and return a collection of 
	 * SearchHit objects with the results of the search query
	 * 
	 * @param ticket Ticket with user information
	 * @param phrase text to search
	 * @param searchType Type of query to be performed. It can be one of this values :
	 * <code>SearchService.SEARCH_CONTENT</code> or <code>SearchService.SEARCH_KEYWORDS</code>
	 * 
	 * @return Collection of SearchHit objects with the results of the search query
	 * 
	 * @throws SearchException If the search can't be performed
	 */
	public Collection search(Ticket ticket,
							 String phrase,
							 String searchType) throws SearchException;		
	
	/**
	 * Performs an XPath query based on JCR XPath. The only thing needed 
	 * are the constraints part. Example: 
	 * [jcr:contains(@jlib:keywords,'test') and @jlib:active='true']
	 * 
	 * @param ticket Ticket with user information
	 * @param xpathQuery XPath query
	 * 
	 * @return Collection of SearchHit objects with the results of the search query
	 * 
	 * @throws SearchException If the search can't be performed
	 */
	public Collection search(Ticket ticket, 
			 				 String xpathQuery) throws SearchException;	
}
