/*
 * jLibrary, Open Source Document Management System
 * 
 * Copyright (c) 2003-2006, Martín Pérez Mariñán, and individual contributors as
 * indicated by the @authors tag. See copyright.txt in the distribution for a
 * full listing of individual contributors. All rights reserved.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the Modified BSD License as published by the Free Software
 * Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the Modified BSD License for more details.
 * 
 * You should have received a copy of the Modified BSD License along with this
 * software; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
package org.jlibrary.core.entities;

/**
 * @author Martin Perez
 * 
 * Profile for server connections
 */
public interface ServerProfile {

  /**
   * @return Returns the location.
   */
  public String getLocation();

  /**
   * @param location
   *          The location to set.
   */
  public void setLocation(String location);

  /**
   * @return Returns the name.
   */
  public String getName();

  /**
   * @param name
   * The name to set.
   */
  public void setName(String name);
  
  /**
   * Optional.
   * <br/>
   * This method will return the services factory used for loading the services 
   * that this server profile must use. If this method returns <code>null</code> 
   * then the default factory.properties file will be used.
   * 
   * @return String Services factory implementation
   */
  public String getServicesFactory();
  
  /**
   * Returns <code>true</code> if this profile points to the local server or
   * <code>false</code> otherwise
   * 
   * @return boolean <code>true</code> if this profile points to the local server or
   * <code>false</code> otherwise
   */
  public boolean isLocal();
}
