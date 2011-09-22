package org.jlibrary.core.factory;

import org.jlibrary.core.repository.RepositoryService;
import org.jlibrary.core.search.SearchService;
import org.jlibrary.core.security.SecurityService;

public interface ServicesFactory {

	public RepositoryService getRepositoryService();	
	public SecurityService getSecurityService();	
	public SearchService getSearchService();	
}
