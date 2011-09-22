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
package org.jlibrary.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.jlibrary.core.entities.Directory;
import org.jlibrary.core.entities.Document;
import org.jlibrary.core.entities.Repository;
import org.jlibrary.core.entities.ResourceNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author martin
 *
 * Utilities to handle files
 */
public class FileUtils {

	static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	/**
	 * Copies a file to a destination. The file can be moved based on 
	 * properties values
	 * 
	 * @param source File to move/copy
	 * @param destination Destination file
	 * @param moveFiles <code>true</code> if the original files must be deleted
	 */
	public static void copyFile(File source, 
						  		File destination, 
						  		boolean moveFiles) throws IOException {
				
		if (source.isDirectory()) {
			// First we copy the directory
			if (!destination.exists()) {
				destination.mkdir();
			}
			
			// We're copying a directory. So we must create it in the destination
			// folder. After this step, the current destination will be the 
			// new created subdirectory
			destination = new File(destination.getAbsolutePath() +
								   System.getProperty("file.separator") +
								   source.getName());
			
			if (!destination.exists()) {
				destination.mkdir();
			}
 
			
			// Now it's recursivity time			
			File[] children = source.listFiles();
			for (int i = 0; i < children.length; i++) {
				String path = destination.getAbsolutePath();
				copyFile(children[i], new File(path), moveFiles);				
			}
		} else {
			if (destination.isDirectory()) {
				org.apache.commons.io.FileUtils.copyFileToDirectory(
						source,destination);
			} else {
				org.apache.commons.io.FileUtils.copyFile(
						source,destination);
			}
				
		}
		if (moveFiles) {
			try {
				org.apache.commons.io.FileUtils.forceDelete(source);
			} catch (IOException ioe) {
				logger.error(ioe.getMessage(),ioe);
			}
		}		
	}

	/**
	 * Copies a single file from source to destination
	 * 
	 * @param source Source of the file
	 * @param destination Destination of the file
	 * @throws IOException If the file can't be copied
	 */
	public static void copyFile(InputStream source, 
								OutputStream destination)
								throws IOException {
									
		// do our own buffering; reuse the same buffer.
		byte[] buffer = new byte[16384];				
							

		// repeat reading into buffer and writing buffer to file,
		// until done.  count will always be # bytes read, until
		// EOF when it is -1.
		int count;
		while((count=source.read(buffer)) != -1)
		  destination.write(buffer, 0, count);

		source.close();
		destination.close();
	}

	/**
	 * Removes a file
	 * 
	 * @param source File to remove
	 */
	public static void removeFile(File source) throws IOException {
			
		try {
			if (source.isDirectory()) {
				// Recursivity	
				File[] children = source.listFiles();
				for (int i = 0; i < children.length; i++) {
					removeFile(children[i]);				
				}
				org.apache.commons.io.FileUtils.forceDelete(source);
			} else {
				org.apache.commons.io.FileUtils.forceDelete(source);	
			}
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
		}
	}

	/**
	 * Returns the path of a directory in the operating system
	 * 
	 * @param directory Directory which we want to obtain its path
	 * @return External path of the document
	 */
	public static String getExternalPath(Repository repository, Directory dir) {
		
		return 	repository.getPath() + 
				dir.getPath();	
	}


	/**
	 * Returns the path of a document in the operating system
	 * 
	 * @param doc Document which we want to obtain its path
	 * @return External path of the document
	 */
	public static String getExternalPath(Repository repository, Document doc) {
		
		if (doc.isExternal()) {
			return doc.getPath();
		} else {
			return repository.getPath() + doc.getPath();
		}
	}

	/**
	 * Returns the path of a document in the operating system
	 * 
	 * @param doc Document which we want to obtain its path
	 * @return External path of the document
	 */
	public static String getExternalPath(Repository repository, 
										 ResourceNode resource) {
		
		return repository.getPath() + resource.getPath();
	}			

	/**
	 * Returns the external file representing a directory
	 * 
	 * @param directory Directory which we want to obtain its path
	 * @return External file of the directory
	 */
	public static File getExternalFile(Repository repository, Directory dir) {
		
		return new File(getExternalPath(repository, dir));
	}

	/**
	 * Returns the external file representing a resource
	 * 
	 * @param directory Resource which we want to obtain its path
	 * @return External file of the resource
	 */
	public static File getExternalFile(Repository repository, 
									   ResourceNode resource) {
		
		return new File(getExternalPath(repository, resource));
	}
	
	/**
	 * Returns a File representing a document
	 * 
	 * @param doc Document which we want to obtain its path
	 * @return File 
	 */
	public static File getExternalFile(Repository repository, Document doc) {
		
		return new File(getExternalPath(repository, doc));
	}

	/**
	 * Changes a file location
	 * 
	 * @param source File to move
	 * @param destination Destination of the file
	 * @param overwerite Tells us to overwrite destination
	 */
	public static void moveFile(File source, 
								File destination,
								boolean overwrite) throws IOException {
		
		copyFile(source,destination,true);
	}

	/**
	 * Removes a directory recursively
	 * 
	 * @param directory Directory to remove
	 */
	public static boolean removeDirectory(File directory) {
		
		if (directory.isFile()) {
			return directory.delete();
		} else {
			File[] files = directory.listFiles();
			if (files == null) return true;
			for (int i = 0; i < files.length; i++) {
				if (removeDirectory(files[i]) == false) {
					return false;
				}
			}
		}
		directory.delete();
		return true;		
	}
	
	public static String getExtension(String path) {
		
		int index = path.lastIndexOf(".");
		if (index == -1) {
			return null;
		}
		return path.substring(index,path.length());
	}
	
	public static String getFileName(String path) {
		
		int index1 = path.lastIndexOf("/");
		int index2 = path.lastIndexOf("\\");
		int index = index1;
		if (index2 > index) {
			index = index2;
		}
		if (index == -1) {
			return path;
		}
		return path.substring(index+1,path.length());
	}	
	
	/**
	 * Builds a path for a document within a directory
	 * 
	 * @param directory Parent directory
	 * @param file Filename
	 * 
	 * @return String Document's path inside that directory
	 */
	public static final String buildPath(Directory directory, 
									     String filename) {
		
		if (directory.getParent() == null) {
			return "/" + filename;
		} else {
			return directory.getPath() + "/" + filename;
		}
	}
		
	public static String obtainAvailableDirectoryName(File parent) {
		
		File[] dirs = parent.listFiles();
		ArrayList ids = new ArrayList();
		for (int i = 0; i < dirs.length; i++) {
			File child = dirs[i];
			if (!child.isDirectory()) continue;
			if (child.getName().startsWith(".")) continue;
			ids.add(new Integer(Integer.parseInt(child.getName())));
		}
		Collections.sort(ids);
		
		String dirId = null;
		for (int i = 0;i<ids.size();i++) {
			Integer id = (Integer)ids.get(i);
			if (!(id.intValue() == i)) {
				dirId = String.valueOf(i);
			}
		}
		if (dirId == null) {
			dirId = String.valueOf(ids.size());
		}
		
		return dirId;
	}
	
	/**
	 * Creates a directory with an unique directory id. This id will be used 
	 * for directory names under the repository structure. The algorithm tries 
	 * to fill empty ids left when directories are removed
	 * 
	 * @param parent Parent file
	 * @return File new created directory
	 */
	public static synchronized File createNewDirectoryName(File parent) 
				throws IOException {
		
		String dirId = obtainAvailableDirectoryName(parent);
		
		File newDir = new File(parent,dirId);
		if (!newDir.mkdir()) {
			throw new IOException("Directory " + 
								  newDir.getAbsolutePath() + 
								  " could not be created");	
		}
		
		return newDir;
	}
}
