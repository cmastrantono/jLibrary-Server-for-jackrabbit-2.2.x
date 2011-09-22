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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.jlibrary.core.config.SessionConfigurationReader;
import org.jlibrary.core.entities.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class stores mappings between client sessions and JSR-170 session
 * objects
 * 
 * @author martin
 *
 */
public class SessionManager {

	static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	
	private static SessionManager instance = new SessionManager();
	
	private javax.jcr.Repository repository;
	
	private ConcurrentHashMap<Ticket, SessionEntry> sessions = 
		new ConcurrentHashMap<Ticket, SessionEntry>();
	
	
	private List<SessionManagerListener> listeners = 
		new ArrayList<SessionManagerListener>();
	
	/**
	 * Singleton
	 *
	 */
	private SessionManager() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Starting session manager");
		}
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		if (logger.isDebugEnabled()) {
			logger.debug("Scheduling session eviction thread");
		}
		
		long executerInitialDelay = Long.parseLong(SessionConfigurationReader.getString("executer.initial.delay"));
		long executerPeriod = Long.parseLong(SessionConfigurationReader.getString("executer.period"));
		
		service.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (logger.isDebugEnabled()) {
					logger.debug("Running session cleaning process. There are " + sessions.size() + " opened sessions");
				}
				
				long now = System.currentTimeMillis();
				long maxSessionInactiveTimeout = Long.parseLong(SessionConfigurationReader.getString("max.session.inactive.timeout"));
				
				// synchronized block
				synchronized(instance){
					Iterator<Map.Entry<Ticket, SessionEntry>> it = sessions.entrySet().iterator();
					
					while(it.hasNext()) {
						Map.Entry<Ticket, SessionEntry> entry = it.next();
						if (now - entry.getValue().getLastUsed() > maxSessionInactiveTimeout) {
							if (logger.isDebugEnabled()) {
								logger.debug("Evicting session for ticket: " + entry.getKey().getId() + ", user:" + entry.getKey().getUser().getName());
							}
							
							if(entry.getValue().getSession() != null){
								entry.getValue().getSession().logout();				
							}
							
							if(entry.getValue().getSystemSession() != null){
								entry.getValue().getSystemSession().logout();
							}
							
							it.remove();
							for (SessionManagerListener listener: listeners) {							
								listener.sessionRemoved(entry.getKey());
							}
							
						}
					}// while
				}
			}
		}, executerInitialDelay, executerPeriod, TimeUnit.SECONDS);
	}

	public javax.jcr.Session getSystemSession(Ticket ticket) {
		if(ticket == null)
			throw new  IllegalArgumentException("Not valid ticket");

		Session session = null;
		
		// synchronized block
		synchronized(instance){
			SessionEntry entry = sessions.get(ticket);
			
			if (entry == null) {
				return null;
			}
			entry.setLastUsed(System.currentTimeMillis());		
			session =entry.getSystemSession();
		}
		
		return session;
	}


	public void setRepository(javax.jcr.Repository repository) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Setting repository");
		}
		synchronized(instance){
			this.repository = repository;			
		}
	}
	
	public javax.jcr.Repository getRepository() {
		
		return repository;
	}
	
	/**
	 * Attachs a JSR-170 session to an user
	 * 
	 * @param ticket Ticket with user information
	 * @param session JSR-170 session instance
	 */
	public void attachSession(Ticket ticket, Session session) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Attaching session for ticket : " + ticket.getId() + ", user:" + ticket.getUser().getName());
		}

		// synchronized block
		synchronized(instance){
			if(sessions.get(ticket) == null){
				// no existe ticket
				SessionEntry entry = new SessionEntry();
				entry.setLastUsed(System.currentTimeMillis());
				entry.setSession(session);
				sessions.put(ticket,entry);
				
				for (SessionManagerListener listener: listeners) {
					listener.sessionAdded(ticket);
				}
			}
			else{
				// ya existe ticket actualizo valor de la session
				sessions.get(ticket).setSession(session);
			}
		}
		
	}

	public void attachSystemSession(Ticket ticket, Session systemSession) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Attaching system session for ticket : " + ticket.getId() + ", user:" + ticket.getUser().getName());
		}

		// synchronized block
		synchronized(instance){
			if(sessions.get(ticket) == null){
				// no existe ticket
				SessionEntry entry = new SessionEntry();
				entry.setLastUsed(System.currentTimeMillis());
				entry.setSystemSession(systemSession);
				sessions.put(ticket,entry);
				
				for (SessionManagerListener listener: listeners) {
					listener.sessionAdded(ticket);
				}
			}
			else{
				// ya existe ticket actualizo valor de system session
				sessions.get(ticket).setSystemSession(systemSession);
			}
		}
	}

	/**
	 * Removes a JSR-170 session for an user
	 * 
	 * @param ticket Ticket with user information
	 */
	public void dettach(Ticket ticket) {
		
		if (logger.isDebugEnabled()) {
			logger.debug("Dettaching session for ticket : " + ticket.getId() + ", user:" + ticket.getUser().getName());
		}
		
		// synchronized block
		synchronized(instance){
			
			SessionEntry entry = sessions.get(ticket);
			if (entry != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Logging out session for ticket: " + ticket.getId() + ", user:" + ticket.getUser().getName());
				}
				if(entry.getSession() != null){
					entry.getSession().logout();				
				}
				
				if(entry.getSystemSession() != null){
					entry.getSystemSession().logout();
				}

			}
			sessions.remove(ticket);
			
			for (SessionManagerListener listener: listeners) {
				listener.sessionRemoved(ticket);
			}
		}
	}
	
	/**
	 * Returns a JSR-170 session for a given ticket
	 * 
	 * @param ticket Ticket with user information
	 * 
	 * @return Session JSR-170 compatible session
	 */
	public Session getSession(Ticket ticket) {
		Session result = null;
		
		if(ticket == null)
			throw new  IllegalArgumentException("Not valid ticket");

		// synchronized block
		synchronized(instance){
			SessionEntry entry = sessions.get(ticket);
			if (entry == null) {
				return null;
			}
			entry.setLastUsed(System.currentTimeMillis());
			result = entry.getSession();
		}
		
		return result;
	}
	
	/**
	 * Returns the number of opened sessions on this server
	 * 
	 * @return int Opened session count
	 */
	public int getOpenedSessionCount() {
		return  sessions.size();
	}
	
	public void addSessionManagerListener(SessionManagerListener listener) {
		
		listeners.add(listener);
	}
	
	public void removeSessionManagerListener(SessionManagerListener listener) {
		
		listeners.remove(listener);
	}
	
	public static SessionManager getInstance() {
		
		return instance;
	}
	
	public javax.jcr.Session getNewSystemSession(){

		javax.jcr.Session systemSession = null;		
		try {
			// synchronized block
			synchronized(instance){
				SimpleCredentials creds = new SimpleCredentials("admin", "admin".toCharArray());
				systemSession = repository.login(creds,"system");
			}
        }
		catch(Exception e){
			logger.error("[getNewSystenSession] : " + e.getMessage());			
		}
		return systemSession;
	}
	
	
}
