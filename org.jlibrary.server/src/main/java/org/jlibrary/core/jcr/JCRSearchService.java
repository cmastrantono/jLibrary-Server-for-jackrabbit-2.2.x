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
package org.jlibrary.core.jcr;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;

import org.apache.commons.lang.StringUtils;
import org.jlibrary.core.entities.SearchResult;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchHit;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.search.algorithms.DefaultSearchAlgorithm;
import org.jlibrary.core.search.algorithms.SearchAlgorithm;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the JCR based SearchService implementation. It uses the default 
 * search algorithm implementation.
 * 
 * @author mpermar
 *
 */
public class JCRSearchService implements SearchService {
	
	static Logger logger = LoggerFactory.getLogger(JCRSearchService.class);
	
	public static final int NO_PAGING=-1;
	
	public JCRSearchService() {}
	
	private SearchAlgorithm searchAlgorithm = new DefaultSearchAlgorithm();
	
	//TODO: Test and improve XPath queries
	public Collection search(Ticket ticket, 
							 String phrase, 
							 String searchType) throws SearchException {

		return search(ticket,phrase,searchType,0,99).getItems();
	}
	
	//TODO: Test and improve XPath queries
	public SearchResult search(Ticket ticket, 
							   String phrase, 
							   String searchType,
							   int init,
							   int end) throws SearchException {
		
		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		String query = null;
		if (searchType.equals(SearchService.SEARCH_KEYWORDS)) {
			query = "//element(*,nt:file)[jcr:contains(@jlib:keywords,'" +
				    phrase +
				    "') and @jlib:active='true']";
		} else if (searchType.equals(SearchService.SEARCH_CONTENT)) {
			query = "//element(*,nt:resource)[jcr:contains(.,'" +
					phrase + 
					"') and @jlib:active='true']/(@jlib:description|rep:excerpt(.))";
			
//			query = "select excerpt(.) from nt:resource where contains(., '"+phrase+"') and jlib:active='true'";
			
		}		

		
		if (session == null) {
			throw new SearchException("Session has expired. Please log in again.");
		}
		
		return search(ticket,session,query,init,end);		
	}
	
	public Collection search(Ticket ticket, 
			 				 String xpathQuery) throws SearchException {

		return search(ticket,xpathQuery,NO_PAGING,NO_PAGING).getItems();		
	}
	
	public SearchResult search(Ticket ticket, 
			 				   String xpathQuery,
			 				   int init,
			 				   int end) throws SearchException {

		String query = "//element(*,nt:file)" + xpathQuery; 
		
		javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
		if (session == null) {
			throw new SearchException("Session has expired. Please log in again.");
		}

		return search(ticket,session,query,init,end);
	}	
	
	@SuppressWarnings("unchecked")
	private SearchResult search(Ticket ticket,
							  	javax.jcr.Session session, 
							  	String strQuery,
							  	int init,
							  	int end) throws SearchException {
		SearchHit sh;
		Set<SearchHit> results = new HashSet<SearchHit>();
		SearchResult result = new SearchResult();
		result.setItems(results);
		result.setInit(init);
		result.setEnd(end);
		try {
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			javax.jcr.Node rootNode = JCRUtils.getRootNode(session);
			String rootPath = rootNode.getPath();						
			String statement = "/jcr:root" + rootPath + strQuery;

			javax.jcr.query.Query query = queryManager.createQuery(statement,javax.jcr.query.Query.XPATH);
			QueryResult queryResult = query.execute();
			
			RowIterator it = queryResult.getRows();
			//NodeIterator nodeIterator = queryResult.getNodes();
	
			result.setSize(it.getSize());								
			
			if (init != NO_PAGING) {
				it.skip(init);
				//nodeIterator.skip(init);
			}
			for (RowIterator it2 = it; it2.hasNext(); ) {
				
				if (init != NO_PAGING && end != NO_PAGING) {					
					if (init>end) {
						break;
					}
					init++;
				}
				javax.jcr.query.Row row = it2.nextRow();																													
				
				String textExcerpt = "";
				try {
					Value excerpt = row.getValue("rep:excerpt(.)");
					textExcerpt = excerpt.getString();
				} catch (Exception e) {
					logger.warn("Exception getting excerpt: " + e.getMessage());
				}
												
//				javax.jcr.Node node = (javax.jcr.Node)nodeIterator.nextNode();
				javax.jcr.Node node = row.getNode();
				
				if (node.isNodeType("nt:frozenNode")){ System.out.println("ask1");continue;}
				if (node.isNodeType(JLibraryConstants.CONTENT_MIXIN)) {
					node = node.getParent();
				}
				try {
					if (!JCRSecurityService.canRead(node, ticket.getUser().getId())) {
						System.out.println("ask2");continue;
					}
				} catch (SecurityException se) {
					logger.error(se.getMessage(),se);
					System.out.println("ask3");
					continue;
				}
				
				//double score = row.getValue(JCRConstants.JCR_SCORE).getDouble();
				double score = row.getScore();				

				sh = new SearchHit();
				sh.setRepository(ticket.getRepositoryId());
				sh.setId(node.getIdentifier());
				sh.setName(node.getProperty(JLibraryConstants.JLIBRARY_NAME).getString());
				String path = StringUtils.difference("/" + JLibraryConstants.JLIBRARY_ROOT, node.getPath());						
				sh.setPath(path);
				sh.setImportance(new Integer((int)node.getProperty(	JLibraryConstants.JLIBRARY_IMPORTANCE).getLong()));
				sh.setExcerpt(textExcerpt);
				
				sh.setScore(score);
				results.add(sh);
			}
			
			//TODO: Allow for plugabble search algorithms
			results = searchAlgorithm.filterSearchResults(results);
			
			
		} catch (InvalidQueryException iqe) {
			System.out.println("Error 1 Jlibrary");
			logger.error(iqe.getMessage());
			return result;
		} catch (javax.jcr.RepositoryException e) {
			System.out.println("Error 2 Jlibrary");
			logger.error(e.getMessage(),e);
			throw new SearchException(e);
		}


		return result;
	}	
}
