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

import java.io.File;
import java.util.HashMap;

import javax.naming.NamingException;

import org.jlibrary.core.config.JLibraryProperties;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.compatibility.VersionChecker;
import org.jlibrary.core.jcr.modules.JCRCleanupModule;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This is a JCR-based local security service. This security service will 
 * unregister Jackrabbit repository when no open sessions are hold.</p>
 * 
 * @author martin
 *
 */
public class JCRLocalSecurityService extends JCRSecurityService {

	static Logger logger = LoggerFactory.getLogger(JCRLocalSecurityService.class);
	
	/*
	 * This variable will map repository homes to security service instances.
	 * 
	 * We need to be able to use several security service instances even in 
	 * local mode when the user wants to load different repositories at 
	 * different places.
	 */
	private static HashMap repositoryHomes = new HashMap();
	
	/**
	 * Default Constructor
	 *
	 */
	public JCRLocalSecurityService() {

		this(JLibraryProperties.getProperty(
				JLibraryProperties.JLIBRARY_REPOSITORIES_HOME));
	}
	
	/**
	 * Constructor
	 * 
	 * @param repositoriesHome Creates a local security service against a 
	 * directory.
	 */
	private JCRLocalSecurityService(String repositoriesHome) {
		javax.jcr.Session systemSession = null;
		try {
			this.repositoriesHome = repositoriesHome;
			javax.jcr.Session sessionDescartable = registerRepository(repositoriesHome);
			sessionDescartable.logout();
        	systemSession = checkSystemWorkspace();
	        
	        new JCRCleanupModule().deletePendingWorkspaces(systemSession);
	        new VersionChecker().checkServer(repository,systemSession);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
		} catch (SecurityException se) {
			logger.error(se.getMessage(),se);
		} catch (NamingException ne) {
			logger.error(ne.getMessage(),ne);
		} catch (RepositoryException re) {
			logger.error(re.getMessage(),re);
		}	
		finally{
			if(systemSession != null){
				systemSession.logout();
			}
		}
	}				

	protected javax.jcr.Session registerRepository(String repositoriesHome) 
								throws RepositoryException, NamingException {
		
		//COMENTADO x EK: esto en un jboss no funca, lo ponemos para q por defecto el archivo
		//repository.xml este en bajo el bin del server
		
		//URL url = JCRSecurityService.class.getClassLoader().getResource("repository.xml");
		//if (url == null) {
//			String errorMessage = "Repository home not found: " + repositoriesHome; 
			//logger.error(errorMessage);
			//throw new RepositoryException(errorMessage);
		//}
		//URI uri = null;
		//try {
			//uri = url.toURI();
			//String configFile = new File(uri).getAbsolutePath();
			String configFile = new File("jlibrary/repository.xml").getAbsolutePath();
			return registerRepository(repositoriesHome,configFile);
//		} catch (URISyntaxException e) {
	//		String errorMessage = "Invalid URI: " + uri;
		//	logger.error(errorMessage);
			//throw new RepositoryException(errorMessage);
		//}
	}

	protected javax.jcr.Session registerRepository(String repositoriesHome,
							String configFile) throws RepositoryException, NamingException {
		
		
		return super.registerRepository(repositoriesHome,configFile);

	}
	
	/**
	 * This method additionally will unregister the repository. This is 
	 * needed because we do not want to maintain opened lock files on the 
	 * repository.
	 * 
	 * @see JCRSecurityService#disconnect(Ticket)
	 */
	public void disconnect(Ticket ticket) throws SecurityException {
		
		super.disconnect(ticket);
		
		repositoryHomes.remove(this.repositoriesHome);
		// Force repository unregister
		SessionManager sessionManager = SessionManager.getInstance();
		if (sessionManager.getOpenedSessionCount() == 0) {
			// 31/03/2011 FIXME LS: descomento esto porque si no patea, hay que ver si es necesario hacer el shutdown o ver como incovarlo 
			//shutdownRepository();
		}
	}	
	
	/**
	 * Singleton. Returns a instance of this security service
	 * 
	 * @param repositoriesHome Path to the repositories home
	 * 
	 * @return JCRSecurityService Security service
	 */
	public static JCRSecurityService getInstance(String repositoriesHome) {
		
		JCRLocalSecurityService securityService = 
			(JCRLocalSecurityService)repositoryHomes.get(repositoriesHome);
		if (securityService == null) {
			
			String localHome = JLibraryProperties.getProperty(
								JLibraryProperties.JLIBRARY_REPOSITORIES_HOME);
			if (repositoriesHome.equals(localHome)) {
				instance = new JCRLocalSecurityService(repositoriesHome);
				securityService = (JCRLocalSecurityService)instance;
			} else {
				securityService = new JCRLocalSecurityService(repositoriesHome);
			}
			repositoryHomes.put(repositoriesHome,securityService);
		}
		return securityService;
	}
}
