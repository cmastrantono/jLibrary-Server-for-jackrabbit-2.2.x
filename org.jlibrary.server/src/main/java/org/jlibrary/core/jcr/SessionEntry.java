package org.jlibrary.core.jcr;

public class SessionEntry {

	private long lastUsed;
	private javax.jcr.Session session;
	private javax.jcr.Session systemSession;
	
	public SessionEntry(){
		this.lastUsed = 0;
		this.session = null;
		this.systemSession = null;
	}
	
	public long getLastUsed() {
		return lastUsed;
	}
	public void setLastUsed(long lastUsed) {
		this.lastUsed = lastUsed;
	}
	public javax.jcr.Session getSession() {
		return session;
	}
	public void setSession(javax.jcr.Session session) {
		this.session = session;
	}
	public javax.jcr.Session getSystemSession() {
		return systemSession;
	}
	public void setSystemSession(javax.jcr.Session systemSession) {
		this.systemSession = systemSession;
	}
	
	
}
