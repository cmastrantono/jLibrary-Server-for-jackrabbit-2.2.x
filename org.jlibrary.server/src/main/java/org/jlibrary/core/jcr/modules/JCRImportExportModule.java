/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Mart�n P�rez Mari��n, and individual 
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.Deflater;

import javax.jcr.AccessDeniedException;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.LoginException;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.jlibrary.core.entities.Ticket;
import org.jlibrary.core.jcr.JCRSecurityService;
import org.jlibrary.core.jcr.JCRUtils;
import org.jlibrary.core.jcr.JLibraryConstants;
import org.jlibrary.core.jcr.SessionManager;
import org.jlibrary.core.jcr.compatibility.CompatibilityFilter1_0;
import org.jlibrary.core.jcr.compatibility.VersionChecker;
import org.jlibrary.core.jcr.nodetypes.NodeTypeManager;
import org.jlibrary.core.properties.CustomPropertyDefinition;
import org.jlibrary.core.repository.exception.RecentlyRemovedRepositoryException;
import org.jlibrary.core.repository.exception.RepositoryAlreadyExistsException;
import org.jlibrary.core.repository.exception.RepositoryException;
import org.jlibrary.core.repository.exception.RepositoryNotFoundException;
import org.jlibrary.core.security.SecurityException;
import org.jlibrary.core.util.xml.XMLWriter;
import org.jlibrary.core.util.zip.ZipEntry;
import org.jlibrary.core.util.zip.ZipFile;
import org.jlibrary.core.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * <p>This module will handle all the import/export operations</p>
 * 
 * @author martin
 *
 */
public class JCRImportExportModule {

	static Logger logger = LoggerFactory.getLogger(JCRImportExportModule.class);
	
	/**
	 * @see org.jlibrary.core.repository.RepositoryService#exportRepository(Ticket)
	 */
	public byte[] exportRepository(Ticket ticket) 
											throws RepositoryNotFoundException, 
												   RepositoryException, 
												   SecurityException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}			
			javax.jcr.Node root = 
				JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canRead(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
				
			ByteArrayOutputStream baos1 = null;
			byte[] rootContent;
			try {
				baos1 = new ByteArrayOutputStream();
				session.exportSystemView(JCRUtils.getRootNode(session)
						.getPath(), baos1, false, false);
				rootContent = baos1.toByteArray();
			} finally {
				if (baos1 != null) {
					baos1.close();
				}
			}
			byte[] systemContent;
			ByteArrayOutputStream baos2 = null;
			try {
				baos2 = new ByteArrayOutputStream();
				session.exportSystemView(JCRUtils.getSystemNode(session)
						.getPath(), baos2, false, false);
				systemContent = baos2.toByteArray();
			} finally {
				if (baos2 != null) {
					baos2.close();
				}
			}
			
			String tag = String.valueOf(rootContent.length)+"*";
			byte[] header = tag.getBytes();
			
			byte[] content = new byte[header.length + rootContent.length + systemContent.length];
			System.arraycopy(header,0,
							 content,0,
							 header.length);
			System.arraycopy(rootContent,0,
							 content,header.length,
							 rootContent.length);
			System.arraycopy(systemContent,0,
							 content,header.length + rootContent.length,
							 systemContent.length);
			
			return zipContent(content);
		} catch (PathNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}
	
	public void exportRepository(Ticket ticket, OutputStream stream) 
											throws RepositoryNotFoundException, 
												   RepositoryException, 
												   SecurityException {
		
		try {
			javax.jcr.Session session = SessionManager.getInstance().getSession(ticket);
			if (session == null) {
				throw new RepositoryException("Session has expired. Please log in again.");
			}			
			javax.jcr.Node root = 
				JCRUtils.getRootNode(session);
			if (!JCRSecurityService.canRead(root, ticket.getUser().getId())) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			// Create a temporary file with content
			File tempRoot = File.createTempFile("tmp", "jlib");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(tempRoot);
				session.exportSystemView(JCRUtils.getRootNode(session)
						.getPath(), fos, false, false);
			} finally {
				if (fos != null) {
					fos.close();	
				}
			}
				
			// Will wrap compression around the given stream			
			ZipOutputStream zos = null;
			try {
				zos = new ZipOutputStream(stream);
				zos.setComment("jLibrary ZIP archive");
				zos.setMethod(ZipOutputStream.DEFLATED);
				zos.setEncoding("UTF-8");
				zos.setLevel(Deflater.DEFAULT_COMPRESSION);

				// create and initialize a zipentry for it
				ZipEntry entry = new ZipEntry("jlibrary");
				entry.setTime(System.currentTimeMillis());
				zos.putNextEntry(entry);
				// write header
				String tag = String.valueOf(tempRoot.length()) + "*";
				byte[] header = tag.getBytes();
				zos.write(header);

				// write root
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(tempRoot);
					IOUtils.copy(fis, zos);
				} finally {
					if (fis != null) {
						fis.close();
					}
				}
				// Delete root file
				tempRoot.delete();

				session.exportSystemView(JCRUtils.getSystemNode(session)
						.getPath(), zos, false, false);
			} finally {
				if (zos != null) {
					zos.closeEntry();
					zos.close();
				}
			}
			
		} catch (PathNotFoundException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		} catch (javax.jcr.RepositoryException e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}

	
	public void importRepository(Ticket ticket, 
								 byte[] zippedContent,
								 String name) 
										throws RepositoryAlreadyExistsException,
											   RepositoryException, 
									   		   SecurityException {
		
		try {
			
			if (!ticket.getUser().isAdmin()) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Session systemSession = SessionManager.getInstance().getSystemSession(ticket);
			WorkspaceImpl workspace = checkWorkspaceExists(name, systemSession);
			// Always change to lowercase
			name = name.toLowerCase();
			workspace.createWorkspace(name);
			
			javax.jcr.Repository repository = SessionManager.getInstance().getRepository();
			SimpleCredentials creds =
			    new SimpleCredentials("username", "password".toCharArray());
			
			systemSession = repository.login(creds,name);
			
			byte[] content = unzipContent(zippedContent);			
			
			// First read the header
			int i = 0;
			while (content[i] != '*') {
				i++;
			}
			i++;
			byte[] header = new byte[i];
			System.arraycopy(content,0,header,0,i);
			String lengthString = new String(header);
			int contentLength = Integer.parseInt(
						lengthString.substring(0,lengthString.length()-1));
			
			// Now, load root content
			
			byte[] rootContent = new byte[contentLength];			
			System.arraycopy(content,i,rootContent,0,contentLength);
			rootContent = filterForCompatibility(rootContent);
			ByteArrayInputStream bais1 = new ByteArrayInputStream(rootContent);
			
			// Now load system content
			byte[] systemContent = new byte[content.length - 
			                                contentLength - 
			                                header.length];
			System.arraycopy(content,header.length + contentLength,
							 systemContent,0,
							 systemContent.length);
			systemContent = filterForCompatibility(systemContent);			
			ByteArrayInputStream bais2 = new ByteArrayInputStream(systemContent);

			systemSession.importXML("/",
					  bais1,
					  ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);

			systemSession.importXML("/",
					  bais2,
					  ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
			
			checkCustomProperties(systemSession);
			
			
			bais1.close();
			bais2.close();
						
			systemSession.save();
			
			// Finally check versions compatibility
			VersionChecker checker = new VersionChecker();
			checker.checkSession(systemSession);
		} catch (ConfigurationException ce) {
			//TODO: Remove this catch block when Jackrabbit supports workspace deletes
			throw new RecentlyRemovedRepositoryException();			
		} catch (RepositoryAlreadyExistsException raee) {
			throw raee;
		} catch (SecurityException se) {
			throw se;
		} catch (AccessDeniedException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (LoginException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}	

	
	public void importRepository(Ticket ticket, 
								 String name,
								 InputStream inputStream) 
										throws RepositoryAlreadyExistsException,
											   RepositoryException, 
									   		   SecurityException {
		
		try {
			
			if (!ticket.getUser().isAdmin()) {
				throw new SecurityException(SecurityException.NOT_ENOUGH_PERMISSIONS);
			}
			
			javax.jcr.Session systemSession = SessionManager.getInstance().getSystemSession(ticket);			
			WorkspaceImpl workspace = checkWorkspaceExists(name, systemSession);
			// Always change to lowercase
			name = name.toLowerCase();
			workspace.createWorkspace(name);
			
			javax.jcr.Repository repository = SessionManager.getInstance().getRepository();
			SimpleCredentials creds =
			    new SimpleCredentials("username", "password".toCharArray());
			
			systemSession = repository.login(creds,name);
			
			// Copy to temp file. We cannot wrap to zip input stream due to incompatibilities
			// between apache implementation and java.util implementation
			File tempFile = File.createTempFile("jlib","tmp");
			tempFile.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempFile);
			IOUtils.copy(inputStream, fos);
			fos.flush();
			fos.close();

			ZipFile archive = null;
			try {
				archive = new ZipFile(tempFile);
			} catch (IOException ioe) {
				logger.warn(
					"[JCRImportService] Trying to import non zipped repository");
				// probably this will be an old repository, so we will return the 
				// content to let the process try to import it
				return;
			}

			// do our own buffering; reuse the same buffer.
			byte[] buffer = new byte[16384];
			ZipEntry entry = archive.getEntry("jlibrary");

			// get a stream of the archive entry's bytes
			InputStream zis = archive.getInputStream(entry);

			//ZipInputStream zis = new ZipInputStream(inputStream);			
			byte[] smallBuffer = new byte[32];
			int i=0;
			boolean tagFound = false;
			while(!tagFound) {
				byte next = (byte)zis.read();
				if (next == '*') {
					tagFound = true;
				} else {
					smallBuffer[i] = next;
					i++;
				}				
			}			

			byte[] header = new byte[i];
			System.arraycopy(smallBuffer,0,header,0,i);
			String lengthString = new String(header);
			int contentLength = Integer.parseInt(
						lengthString.substring(0,lengthString.length()));
			
			InputStream wrapzis = new ImportInputStream(zis,contentLength);
			systemSession.importXML("/",
					  wrapzis,
					  ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);			

			// Reopen the stream. importXML closes it
			zis = archive.getInputStream(entry);
			zis.skip(i+1+contentLength);
			
			// Now import the remaining info
			systemSession.importXML("/",
					  zis,
					  ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
			
			tempFile.delete();
			
			checkCustomProperties(systemSession);
						
			systemSession.save();
			
			// Finally check versions compatibility
			VersionChecker checker = new VersionChecker();
			checker.checkSession(systemSession);
		} catch (ConfigurationException ce) {
			//TODO: Remove this catch block when Jackrabbit supports workspace deletes
			throw new RecentlyRemovedRepositoryException();			
		} catch (RepositoryAlreadyExistsException raee) {
			throw raee;
		} catch (SecurityException se) {
			throw se;
		} catch (AccessDeniedException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (LoginException e) {
			logger.error(e.getMessage(),e);
			throw new SecurityException(e);
		} catch (RepositoryException re) {
			throw re;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new RepositoryException(e);
		}
	}	
	
	private void checkCustomProperties(Session session) throws javax.jcr.RepositoryException {

		NodeTypeManager nodetypeManager = new NodeTypeManager();
		// Check and create required custom properties
		javax.jcr.Node systemNode = JCRUtils.getSystemNode(session);
		if (systemNode.hasNode(JLibraryConstants.JLIBRARY_CUSTOM_PROPERTIES)) {
			javax.jcr.Node propertiesNode = systemNode.getNode(JLibraryConstants.JLIBRARY_CUSTOM_PROPERTIES);
			NodeIterator it = propertiesNode.getNodes();
			while (it.hasNext()) {
				javax.jcr.Node propsNode = (javax.jcr.Node) it.next();
				CustomPropertyDefinition propdef = new CustomPropertyDefinition();
				propdef.setName(propsNode.getProperty(
								JLibraryConstants.JLIBRARY_PROPERTY_NAME).getString());
				propdef.setType((int)propsNode.getProperty(
						JLibraryConstants.JLIBRARY_PROPERTY_TYPE).getLong());
				propdef.setMultivalued(propsNode.getProperty(
						JLibraryConstants.JLIBRARY_PROPERTY_MULTIVALUED).getBoolean());
				propdef.setAutocreated(propsNode.getProperty(
						JLibraryConstants.JLIBRARY_PROPERTY_AUTOCREATED).getBoolean());
				if (propsNode.hasProperty(JLibraryConstants.JLIBRARY_PROPERTY_DEFAULT)) {
					propdef.setDefaultValues(propsNode.getProperty(
							JLibraryConstants.JLIBRARY_PROPERTY_DEFAULT).getValues());	
				}
				
				logger.info("Registering property : " + propdef);
				nodetypeManager.registerCustomProperty(session, propdef);
			}			
		}
		
	}


	private byte[] filterForCompatibility(byte[] content) 
													throws RepositoryException {
		
		try {
			//logger.info(IOUtils.toString(content));
            XMLReader parser = XMLReaderFactory.createXMLReader(
            							"org.apache.xerces.parsers.SAXParser");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            CompatibilityFilter1_0 filter = new CompatibilityFilter1_0();
	        filter.setParent(parser);
	        filter.setContentHandler(
	        	       new XMLWriter(new OutputStreamWriter(baos)));	   
	        
	        filter.parse(new InputSource(new ByteArrayInputStream(content)));      
	        
	        baos.flush();
	        content = baos.toByteArray();
			//logger.info(IOUtils.toString(content));
        
			return content;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}	
	}


	/**
	 * Checks if a given workspace exists
	 * 
	 * @param name Name of the workspace to check
	 * @param systemSession System session
	 * 
	 * @return WorkspaceImpl A WorkspaceImpl if the workspace currently exists
	 * 
	 * @throws javax.jcr.RepositoryException If the check cannot be done
	 * @throws RepositoryAlreadyExistsException If the workspace already exists
	 * @throws RecentlyRemovedRepositoryException If the workspace already 
	 * exists, but has been tagged to be definitvely removed on server startup
	 */
	public WorkspaceImpl checkWorkspaceExists(String name, 
			   								  Session systemSession) 
									 throws javax.jcr.RepositoryException, 
			   						   		RepositoryAlreadyExistsException,
			   						   		RecentlyRemovedRepositoryException {
		
		WorkspaceImpl workspace = (WorkspaceImpl)systemSession.getWorkspace();
		String[] names = workspace.getAccessibleWorkspaceNames();
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(name)) {
				// Ok, check if it was scheduled for remove
				if (JCRCleanupModule.isScheduledForDelete(systemSession,name)) {
					throw new RecentlyRemovedRepositoryException();
				}
				throw new RepositoryAlreadyExistsException();
			}
		}
		return workspace;
	}
	
	/**
	 * Zips a given file or directory
	 * 
	 * @param byte[] content to zip
	 * 
	 * @return byte[] zipped content
	 * 
	 * @throws IOException If the zip content can't be created
	 */
	private byte[] zipContent(byte[] content) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		zos.setComment("jLibrary ZIP archive");
		zos.setMethod(ZipOutputStream.DEFLATED);
		zos.setEncoding("UTF-8");
		zos.setLevel(Deflater.DEFAULT_COMPRESSION);			

		// create and initialize a zipentry for it
		ZipEntry entry =  new ZipEntry("jlibrary");
		entry.setTime(System.currentTimeMillis());
		zos.putNextEntry(entry);
		zos.write(content);
		zos.closeEntry();	
		
		zos.close();
		baos.close();
		
		return baos.toByteArray();
	}


	/**
	 * Unzips come content and returns its contents
	 * 
	 * @param content[] Content to unzip
	 * 
	 * @return byte[] Unzipped content
	 * 
	 * @throws IOException If the file can't be unzipped
	 */
	private byte[] unzipContent(byte[] content) throws IOException {

		File tempFile = File.createTempFile("jlib","tmp");
		tempFile.deleteOnExit();
		FileOutputStream fos = new FileOutputStream(tempFile);
		fos.write(content);
		fos.flush();
		fos.close();

		ZipFile archive = null;
		try {
			archive = new ZipFile(tempFile);
		} catch (IOException ioe) {
			logger.warn(
				"[JCRImportService] Trying to import non zipped repository");
			// probably this will be an old repository, so we will return the 
			// content to let the process try to import it
			return content;
		}

		// do our own buffering; reuse the same buffer.
		byte[] buffer = new byte[16384];
		ZipEntry entry = archive.getEntry("jlibrary");

		// get a stream of the archive entry's bytes
		InputStream in = archive.getInputStream(entry);

		// open a stream to the destination buffer
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		// repeat reading into buffer and writing buffer to file,
		// until done.  count will always be # bytes read, until
		// EOF when it is -1.
		int count;
		while((count=in.read(buffer)) != -1)
		  out.write(buffer, 0, count);

		in.close();
		out.close();
		tempFile.delete();
		
		return out.toByteArray();
	}	
}
