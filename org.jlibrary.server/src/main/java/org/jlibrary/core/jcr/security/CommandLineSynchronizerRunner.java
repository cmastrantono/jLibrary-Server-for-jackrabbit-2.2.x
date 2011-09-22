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
package org.jlibrary.core.jcr.security;

import java.io.File;
import java.util.Hashtable;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jackrabbit.core.jndi.RegistryHelper;
import org.jlibrary.core.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This command line application will launch a synchronizer to consolidate 
 * jLibrary security system and a third party authorization system
 * 
 * @author martin
 *
 */
public class CommandLineSynchronizerRunner {

	static Logger logger = LoggerFactory.getLogger(CommandLineSynchronizerRunner.class);
	
	private static void printUsageAndExit() {
		
		System.out.println("Usage: CommandLineSynchronization synchronization-class-name jlibrary-workspace user password repositories-home config-file");
		System.out.println();
		System.out.println("Where:");
		System.out.println("synchronization-class: Synchronizer to execute");
		System.out.println("jlibrary-workspace: jLibrary workspace to synchronize");
		System.out.println("user: Name of an user with administrative rights on the workspace");
		System.out.println("password: Password for the given user");
		System.out.println("repositories-home: Location of the directory in which the jLibrary repository is stored");
		System.out.println("config-file: Repository.xml configuration file path");
		System.out.println();
		System.out.println("Example: CommandLineSynchronization my.company.ldap.LDAPSynchronizer myWorkspace admin changeme c:\\temp\\repositories c:\\temp\\conf\\repository.xml");
		System.exit(0);
	}
	
	public static void main(String[] args) {

		if (!(args.length == 6)) {
			printUsageAndExit();
		}
		
		String className = args[0];
		String workspaceName = args[1];
		String repositoriesHome = args[4];
		String configFile = args[5];
		SimpleCredentials credentials = 
			new SimpleCredentials(args[2],args[3].toCharArray());
		Synchronizer synchronizer = null;
		try {
			synchronizer = (Synchronizer)Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage(),e);
			System.exit(0);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(),e);
			System.exit(0);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(),e);
			System.exit(0);
		}
		
		if (!(new File(repositoriesHome).exists())) {			
			System.out.println("Repositories home does not exist");
			printUsageAndExit();
		}
		if (!(new File(configFile).exists())) {			
			System.out.println("Configuration file does not exist");
			printUsageAndExit();
		}		
		try {
			Repository repository = registerRepository(repositoriesHome,configFile);
			synchronizer.synchronize(repository,workspaceName,credentials);
		} catch (NamingException e) {
			logger.error(e.getMessage(),e);
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
		}
	}

	protected static Repository registerRepository(
			String repositoriesHome,
			String configFile) throws NamingException {
		
		Repository repository;
		
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
		        "org.apache.jackrabbit.core.jndi.provider.DummyInitialContextFactory");
		env.put(Context.PROVIDER_URL, "localhost");
		
		InitialContext ctx = new InitialContext(env);
		try {
			RegistryHelper.registerRepository(
					ctx, "jlibrary.repository", configFile, repositoriesHome, true);
		    repository = (javax.jcr.Repository) ctx.lookup("jlibrary.repository");

		} catch (RepositoryException re) {
			logger.error(re.getMessage(),re);
		    repository = (javax.jcr.Repository) ctx.lookup("jlibrary.repository");	        	
		}
		
		return repository;
	}	
}
