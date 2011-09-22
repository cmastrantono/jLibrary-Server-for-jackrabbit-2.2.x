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
package org.jlibrary.core.jcr.modules;

import java.io.File;
import java.io.IOException;

import javax.jcr.Value;

import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class will handle some repository cleanup operations like remove 
 * deleted workspaces and things like these.</p>
 * 
 * @author martin
 *
 */
public class JCRCleanupModule {

	static Logger logger = LoggerFactory.getLogger(JCRCleanupModule.class);
	
	/**
	 * Schedules a repository for delete on jLibrary startup
	 * 
	 * @param session Repository session
	 * @param workspacePath Path for the workspace files
	 */
	public void scheduleForDelete(javax.jcr.Session session, 
								  String repositoryName,
								  String workspacePath) 
												throws RepositoryException {
		
		try {
			logger.debug(
				"[CleanupModule] Scheduling '" + workspacePath+"' for delete");

			javax.jcr.Node systemRoot = session.getRootNode();
			javax.jcr.Property scheduled = systemRoot.getProperty(
									JLibraryConstants.JLIBRARY_DELETE_SCHEDULE);

			
			JCRUtils.addToProperty(scheduled,repositoryName+","+workspacePath);		
			session.save();
		} catch (javax.jcr.RepositoryException e) {			
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		
	}
	

	/**
	 * Tells if a repository name has been scheduled for delete on repository 
	 * startup
	 * 
	 * @param session Repository session
	 * @param name Repository name
	 * 
	 * @return boolean <code>true</code> if the repository has been scheduled 
	 * for delete on repository startup and <code>false</code> otherwise.
	 */
	public static boolean isScheduledForDelete(
						javax.jcr.Session systemSession, 
						String name) throws javax.jcr.RepositoryException {
		
		javax.jcr.Node systemRoot = systemSession.getRootNode();
		javax.jcr.Property scheduled = systemRoot.getProperty(
								JLibraryConstants.JLIBRARY_DELETE_SCHEDULE);

		Value[] workspaceData = scheduled.getValues();
		for (int i = 0; i < workspaceData.length; i++) {
			String data = workspaceData[i].getString();
			int j = data.indexOf(",");
			String repositoryName = data.substring(0,j);
			if (repositoryName.equals(name)) {
				return true;
			}
		}
		return false;		
	}
	
	/**
	 * Deletes workspaces that were tagged to be deleted and are pending to be 
	 * phisically deleted
	 * 
	 * @param systemSession Repository session to use for deletes
	 */
	public void deletePendingWorkspaces(javax.jcr.Session systemSession) 
												throws RepositoryException {
		
		try {
			logger.info(
					"[CleanupModule] Deleting pending workspaces");
			javax.jcr.Node systemRoot = systemSession.getRootNode();
			javax.jcr.Property scheduled = systemRoot.getProperty(
									JLibraryConstants.JLIBRARY_DELETE_SCHEDULE);

			
			Value[] workspaceData = scheduled.getValues();
			try {
			for (int i = 0; i < workspaceData.length; i++) {
				String data = workspaceData[i].getString();
				int j = data.indexOf(",");
				String repositoryPath = data.substring(j+1,data.length());
				File f = new File(repositoryPath);
				try {
					org.apache.commons.io.FileUtils.forceDelete(f);
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}			
			}
			} catch (Exception e) {}
			if (workspaceData.length > 0) {
				scheduled.setValue(new Value[]{});
				systemSession.save();
			}
		} catch (javax.jcr.RepositoryException e) {			
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
		 
	}
}
