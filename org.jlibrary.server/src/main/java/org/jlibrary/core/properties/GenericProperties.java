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
package org.jlibrary.core.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin
 *
 * Generic class for repository properties
 */
public abstract class GenericProperties implements Serializable {
	
	private Map map = new HashMap();
	
	public static final PropertyDef EMPTY_PROPERTY = new PropertyDef() {

		private static final long serialVersionUID = 1L;

		public Object getValue() {
			return null;
		}
	};
	
	public Map getPropertiesMap() {
		
		return map;
	}
	
	public void setPropertiesMap(Map map) {
		
		this.map = map;
	}
	
	/**
	 * Returns the entry set
	 * 
	 * @return Entry set
	 */
	public Set entrySet() {
		
		return new HashSet(map.entrySet());
	}
	
	/**
	 * Returns a property given a key
	 * 
	 * @param key Key of the property
	 * 
	 * @return PropertyDef Property for the given key or <code>null</code> if 
	 * no property can be found. If there is more than one value for the given
	 * property then the first value is return.
	 */
	public PropertyDef getProperty(Object key) {
		
		PropertyDef[] properties = (PropertyDef[])map.get(key);
		if (properties == null) {
			return EMPTY_PROPERTY;
		}
		
		return properties[0];
	}
	
	/**
	 * Returns an array of properties for a given key
	 * 
	 * @param key Key of the property
	 * @return PropertyDef[] Array of properties for the given key or <code>null</code>
	 * if no properties can be found
	 */
	public PropertyDef[] getPropertyList(Object key) {

		Object value = map.get(key);			
		if (value == null) {
			return new PropertyDef[]{};
		}
		return (PropertyDef[])value;
	}
	
	/**
	 * Returns a list with the available custom properties if any. If no custom 
	 * properties are available then an empty list will be returned.
	 * 
	 * @return List List with all the available custom properties
	 * 
	 */
	public List getCustomProperties() {

		ArrayList properties = new ArrayList();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			PropertyDef[] propdef = (PropertyDef[])entry.getValue();
			for (int i=0;i<propdef.length;i++) {
				if (propdef[i].isCustom()) {
					properties.add(propdef[i]);
				}
			}
		}
		return properties;
	}	
	
	/**
	 * Adds a property 
	 * 
	 * @param key Key of the property
	 * @param property Property definition
	 * 
	 * @throws InvalidPropertyTypeException if a property isn't defined correctly
	 * @throws PropertyNotFoundException if a wrong property is passed	 
	 */
	public void put(Object key, PropertyDef property) 
		throws InvalidPropertyTypeException, PropertyNotFoundException {
		
		if (!property.isCustom()) {
			// No type and definition checks are performed over custom properties
			checkPropertyDefinition(key);
			checkType(key,property);
		}
		
		Object value = map.get(key);
		if (value == null) {
			map.put(key, new PropertyDef[]{property});
		} else {
			PropertyDef[] array = (PropertyDef[])value;
			List list = new ArrayList(Arrays.asList(array));
			list.add(property);
			// Put the list
			map.put(key,list.toArray(new PropertyDef[]{}));
		}
	}
	
	/**
	 * Adds an array of properties
	 * 
	 * @param key Key of the property
	 * @param property Array of properties to be added
	 * 
	 * @throws InvalidPropertyTypeException if a property isn't defined correctly
	 * @throws PropertyNotFoundException if a wrong property is passed	 
	 */
	public void put(Object key, PropertyDef[] properties) 
		throws InvalidPropertyTypeException, PropertyNotFoundException {
		
		for (int i = 0; i < properties.length; i++) {
			put(key,properties[i]);
		}
	}

	/**
	 * Checks if a given property exists
	 * 
	 * @param key Key of the property
	 * @return <code>true</code> if the property exists and <code>false</code> otherwise.
	 */
	public boolean containsProperty(Object key) {
		
		return map.containsKey(key);
	}
	
	/**
	 * Return the number of property definitions stored
	 * 
	 * @return Number of property definitions
	 */
	public int size() {
		
		return map.size();
	}
	
	/**
	 * Return the number of values for a given property
	 * 
	 * @param key Key of the property
	 * @return Number of property definitions
	 */
	public int size(Object key) {
		
		Object value = map.get(key);
		if (value == null) {
			return 0;
		}
		PropertyDef[] properties = (PropertyDef[])value;
		return properties.length;
	}
	
	/**
	 * Checks property's definitions
	 * 
	 * @param key Property's key
	 * 
	 * @throws PropertyNotFoundException if a wrong property is passed
	 */
	protected abstract void checkPropertyDefinition(Object key) throws PropertyNotFoundException;
	
	/**
	 * Checks property's type
	 * 
	 * @param key Property's key
	 * @param property property
	 * 
	 * @throws PropertyNotFoundException if property's type is wrong
	 */
	protected abstract void checkType(Object key, PropertyDef property) throws InvalidPropertyTypeException;
	
	/**
	 * Adds a property. If exists another property with that key, then the 
	 * property will be added to the property values list 
	 * 
	 * @param key Key to be used
	 * @param value Value for that key
	 * 
	 * @throws PropertyNotFoundException If the property can't be found
	 * @throws InvalidPropertyTypeException If the property has an invalid type
	 */
	public void addProperty(Object key, Object value) throws PropertyNotFoundException,
													   		 InvalidPropertyTypeException {
		
		if (value != null) {
			PropertyDef property = new PropertyDef();
			property.setKey(key);
			property.setValue(value);
			property.setCustom(false);
			put(key,property);
		}
	}
	
	/**
	 * Adds a custom property. If another property exits with that key, then the 
	 * property will be added to the property values list.
	 * <p/>
	 * Currently, no type checks are performed over custom properties. 
	 * 
	 * @param key Key to be used
	 * @param value Value for that key
	 */
	public void addCustomProperty(Object key, 
								  Object value) {
		
		if (value != null) {
			PropertyDef property = new PropertyDef();
			property.setKey(key);
			property.setValue(value);
			property.setCustom(true);
			try {
				put(key,property);
			} catch (Exception e) {
				// No exceptions will be thrown for custom properties
			}
		}
	}	
	
	/**
	 * Sets a property. If exists another property with that key, then 
	 * the property will be replaced 
	 * 
	 * @param key Key to be used
	 * @param value Value for that key
	 * 
	 * @throws PropertyNotFoundException If the property can't be found
	 * @throws InvalidPropertyTypeException If the property has an invalid type
	 */
	public void setProperty(Object key, Object value) throws PropertyNotFoundException,
													   		 InvalidPropertyTypeException {
		
		if (value != null) {
			PropertyDef nameProperty = new PropertyDef();
			nameProperty.setKey(key);
			nameProperty.setValue(value);
			map.put(key,null);
			put(key,nameProperty);
		}
	}	
	
	/**
	 * Removes a property
	 * 
	 * @param key Property's key
	 */
	public void removeProperty(Object key) {
		
		map.remove(key);
	}
	
	/**
	 * Returns <code>true</code> if the property with the specified key 
	 * exists, and <code>false</code> otherwise
	 * 
	 * @param key Key to be searched
	 * 
	 * @return boolean <code>true</code> if the property with the specified key 
	 * exists, and <code>false</code> otherwise
	 */
	public boolean hasProperty(Object key) {
		
		if (getProperty(key) == EMPTY_PROPERTY) {
			return false;
		}
		return true;
	}
	
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		Iterator it = map.values().iterator();
		while (it.hasNext()) {
      Object obj = it.next();
      
//			PropertyDef property = (PropertyDef)obj;
//			buffer.append("[");
//			buffer.append(property.getKey());
//			buffer.append(",");
//			if (property.getType() != PropertyType.BINARY) {
//				buffer.append(property.getValue());
//			} else {
//				buffer.append("binary");
//			}
//			buffer.append("]");
//		}
//		buffer.append("]");
//		return buffer.toString();
      
      StringBuffer sb = new StringBuffer("");
      if ( obj instanceof PropertyDef[] ){
       
        PropertyDef[] aoProperties = (PropertyDef[]) obj;
       
        sb.append("{");
        for (int i = 0; i < aoProperties.length; i++) {
          sb.append(aoProperties[i].toString());
          if ( i+1 < aoProperties.length ){ sb.append(", "); }
        }
       
        sb.append("}");
       
      }else{
        
        PropertyDef property = (PropertyDef) obj;
        
        sb.append("[");
        sb.append(property.getKey());
        sb.append(",");
        if (property.getType() != PropertyType.BINARY) {
          sb.append(property.getValue());
        } else {
          sb.append("binary");
        }
        sb.append("]");
        
      }
     
      //-- Add the property or list representation
      buffer.append ("\t");
      buffer.append(sb);
      buffer.append("\n");
    }
      
    return buffer.toString();
	}
    
}
