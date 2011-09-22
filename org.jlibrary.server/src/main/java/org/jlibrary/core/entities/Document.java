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
package org.jlibrary.core.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jlibrary.core.properties.DocumentProperties;
import org.jlibrary.core.properties.InvalidPropertyTypeException;
import org.jlibrary.core.properties.PropertyDef;
import org.jlibrary.core.properties.PropertyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Document class
 * 
 * @author mpermar
 *
 */
public class Document extends Node implements Serializable {

	static Logger logger = LoggerFactory.getLogger(Document.class);
	
	static final long serialVersionUID = 8760570708196964404L;
	
	private transient Document backup;
	
	private transient HashSet insertedNotes = new HashSet();
	private transient HashSet deletedNotes = new HashSet();
	private transient HashSet updatedNotes = new HashSet();

	private transient HashSet insertedResources = new HashSet();
	private transient HashSet deletedResources = new HashSet();

	private transient HashSet insertedRelations = new HashSet();
	private transient HashSet deletedRelations = new HashSet();		

	private transient HashSet insertedCategories = new HashSet();
	private transient HashSet deletedCategories = new HashSet();	
	
    private boolean external;

    private boolean reference;

    private org.jlibrary.core.entities.DocumentMetaData metaData;
    
    private Set relations;
    
	private Set resourceNodes;   
	
	private Map customProperties;
    
	/*
	 * Last version id. It's useful to check if a document is cached
	 */
	private String lastVersionId;

	public Document() {
    	
    	setDocument(Boolean.TRUE);
    }

    public boolean isExternal() {
        return this.external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }
    public boolean isReference() {
        return this.reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }
    public org.jlibrary.core.entities.DocumentMetaData getMetaData() {
        return this.metaData;
    }

    public void setMetaData(org.jlibrary.core.entities.DocumentMetaData metaData) {
        this.metaData = metaData;
    }

    public String toString() {
        return getName();
    }
	
	public void addRelation(Relation relation) {
		
	    if (insertedRelations == null) {
	        insertedRelations = new HashSet();
	    }
	    if (deletedRelations == null) {
	        deletedRelations = new HashSet();
	    }
	    
		insertedRelations.add(relation);
		deletedRelations.remove(relation);
		getRelations().add(relation.getDestinationNode());		
	}
	
	public void removeRelation(Relation relation) {
		
	    if (insertedRelations == null) {
	        insertedRelations = new HashSet();
	    }
	    if (deletedRelations == null) {
	        deletedRelations = new HashSet();
	    }
	    
		if (!insertedRelations.contains(relation)) {
			deletedRelations.add(relation);
		}
		insertedRelations.remove(relation);
		getRelations().remove(relation.getDestinationNode());
	}		
	
	public void addNote(Note note) {
		
	    if (insertedNotes == null) {
	        insertedNotes = new HashSet();
	    }
	    if (updatedNotes == null) {
	        updatedNotes = new HashSet();
	    }
	    if (deletedNotes == null) {
	        deletedNotes = new HashSet();
	    }
	    
		insertedNotes.add(note);
		deletedNotes.remove(note);
		getNotes().add(note);
	}
	
	public void updateNote(Note note) {
		
	    if (insertedNotes == null) {
	        insertedNotes = new HashSet();
	    }
	    if (updatedNotes == null) {
	        updatedNotes = new HashSet();
	    }
	    if (deletedNotes == null) {
	        deletedNotes = new HashSet();
	    }
	    
	    if (!insertedNotes.contains(note)) {
	    	updatedNotes.add(note);
	    }
	    insertedNotes.remove(note);
	}	
	
	public void removeNote(Note note) {
		
	    if (insertedNotes == null) {
	        insertedNotes = new HashSet();
	    }
	    if (deletedNotes == null) {
	        deletedNotes = new HashSet();
	    }
	    if (updatedNotes == null) {
	        updatedNotes = new HashSet();
	    }	    
	    
		if (!insertedNotes.contains(note)) {
			deletedNotes.add(note);
		}
		insertedNotes.remove(note);
		updatedNotes.remove(note);
		getNotes().remove(note);
	}
	
	public Set getInsertedNotes() {
		
	    if (insertedNotes == null) {
	        insertedNotes = new HashSet();
	    }
		return insertedNotes; 
	}
	
	public Set getUpdatedNotes() {
		
	    if (updatedNotes == null) {
	        updatedNotes = new HashSet();
	    }
		return updatedNotes; 
	}	
	
	public Set getDeletedNotes() {
		
	    if (deletedNotes == null) {
	        deletedNotes = new HashSet();
	    }
		return deletedNotes;
	}
	
	public void clearInsertedNotes() {
		
	    if (insertedNotes == null) {
	        insertedNotes = new HashSet();
	    }
		insertedNotes.clear();
	}

	public void clearUpdatedNotes() {
		
	    if (updatedNotes == null) {
	        updatedNotes = new HashSet();
	    }
		updatedNotes.clear();
	}	
	
	public void clearDeletedNotes() {
		
	    if (deletedNotes == null) {
	        deletedNotes = new HashSet();
	    }	    
		deletedNotes.clear();
	}
	
	public Set getInsertedRelations() {
		
	    if (insertedRelations == null) {
	        insertedRelations = new HashSet();
	    }
		return insertedRelations; 
	}
	
	public Set getDeletedRelations() {
		
	    if (deletedRelations == null) {
	        deletedRelations = new HashSet();
	    }
		return deletedRelations;
	}	
	
	public void clearInsertedRelations() {
		
	    if (insertedRelations == null) {
	        insertedRelations = new HashSet();
	    }
		insertedRelations.clear();
	}
	
	public void clearDeletedRelations() {
		
	    if (deletedRelations == null) {
	        deletedRelations = new HashSet();
	    }
		deletedRelations.clear();
	}	
	
	public void addResource(ResourceNode resource) {
		
	    if (insertedResources == null) {
	        insertedResources = new HashSet();
	    }
	    if (deletedResources == null) {
	        deletedResources = new HashSet();
	    }
	    
		insertedResources.add(resource);
		deletedResources.remove(resource);
	}
	
	public void removeResource(ResourceNode resource) {
		
	    if (insertedResources == null) {
	        insertedResources = new HashSet();
	    }
	    if (deletedResources == null) {
	        deletedResources = new HashSet();
	    }
	    
		if (!insertedResources.contains(resource)) {
			deletedResources.add(resource);
		}
		insertedResources.remove(resource);	
	}

	public void addCategory(Category category) {
		
	    if (insertedCategories == null) {
	        insertedCategories = new HashSet();
	    }
	    if (deletedCategories == null) {
	        deletedCategories = new HashSet();
	    }
	    
		insertedCategories.add(category);
		deletedCategories.remove(category);
	}
	
	public void removeCategory(Category category) {
		
	    if (insertedCategories == null) {
	        insertedCategories = new HashSet();
	    }
	    if (deletedCategories == null) {
	        deletedCategories = new HashSet();
	    }
	    
		if (!insertedCategories.contains(category)) {
			deletedCategories.add(category);
		}
		insertedCategories.remove(category);	
	}

	
	public Set getInsertedResources() {
		
		return insertedResources; 
	}
	
	public Set getDeletedResources() {
		
		return deletedResources;
	}
	
	public void clearInsertedResources() {
		
	    if (insertedResources == null) {
	        insertedResources = new HashSet();
	    }
	    
		insertedResources.clear();
	}
	
	public void clearDeletedResources() {
		
	    if (deletedResources == null) {
	        deletedResources = new HashSet();
	    }
	    
		deletedResources.clear();
	}
	
		public void clearInsertedCategories() {
		
	    if (insertedCategories == null) {
	        insertedCategories = new HashSet();
	    }
	    
		insertedCategories.clear();
	}
	
	public void clearDeletedCategories() {
		
	    if (deletedCategories == null) {
	        deletedCategories = new HashSet();
	    }
	    
		deletedCategories.clear();
	}
	
	public void saveState() {
		
		backup = new Document();
		backup.setMetaData(new DocumentMetaData());
		
		backup.setDescription(getDescription());
		backup.setName(getName());
		backup.setPosition(getPosition());
		backup.setImportance(getImportance());
		
		Set notes = new HashSet();
		if (getNotes() != null) {
			notes.addAll(getNotes());
		}
		backup.setNotes(notes);
		
		Set relations = new HashSet();
		if (getRelations() != null) {
			relations.addAll(getRelations());
		}
		backup.setRelations(relations);
		
		Set resourceNodes = new HashSet();
		if (getResourceNodes() != null) {
			resourceNodes.addAll(getResourceNodes());
		}
		backup.setResourceNodes(resourceNodes);
		
		backup.getMetaData().setAuthor(getMetaData().getAuthor());
		backup.getMetaData().setKeywords(getMetaData().getKeywords());
		backup.getMetaData().setTitle(getMetaData().getTitle());
		backup.getMetaData().setUrl(getMetaData().getUrl());
		
		clearDeletedNotes();
		clearDeletedResources();
		clearDeletedRelations();
		clearInsertedNotes();
		clearUpdatedNotes();
		clearInsertedResources();
		clearInsertedRelations();
		clearInsertedCategories();
		clearDeletedCategories();
		
	}
	
	public void restoreState() {
		
		getMetaData().setAuthor(backup.getMetaData().getAuthor());
		getMetaData().setKeywords(backup.getMetaData().getKeywords());
		getMetaData().setTitle(backup.getMetaData().getTitle());
		getMetaData().setUrl(backup.getMetaData().getUrl());
		
		setImportance(backup.getImportance());
		setDescription(backup.getDescription());
		setName(backup.getName());
		setPosition(backup.getPosition());
		setNotes(backup.getNotes());
		setResourceNodes(backup.getResourceNodes());
		setRelations(backup.getRelations());
				
		clearDeletedNotes();
		clearDeletedResources();
		clearDeletedRelations();
		clearInsertedNotes();
		clearUpdatedNotes();
		clearInsertedResources();
		clearInsertedRelations();
		clearInsertedCategories();
		clearDeletedCategories();
		
	}
	/**
	 * @return Returns the relations.
	 */
	public Set getRelations() {
		return relations;
	}

	/**
	 * @param relations The relations to set.
	 */
	public void setRelations(Set relations) {
		this.relations = relations;
	}

	public DocumentProperties dumpProperties() {
		
		DocumentProperties properties = new DocumentProperties();
		try {
			properties.put(DocumentProperties.DOCUMENT_NAME, 
						   new PropertyDef(DocumentProperties.DOCUMENT_NAME,getName()));
			properties.put(DocumentProperties.DOCUMENT_DESCRIPTION, 
					   new PropertyDef(DocumentProperties.DOCUMENT_DESCRIPTION,getDescription()));
			properties.put(DocumentProperties.DOCUMENT_IMPORTANCE, 
					   new PropertyDef(DocumentProperties.DOCUMENT_IMPORTANCE,getImportance()));
			properties.put(DocumentProperties.DOCUMENT_KEYWORDS, 
					   new PropertyDef(DocumentProperties.DOCUMENT_KEYWORDS,getMetaData().getKeywords()));
			properties.put(DocumentProperties.DOCUMENT_TITLE, 
					   new PropertyDef(DocumentProperties.DOCUMENT_TITLE,getMetaData().getTitle()));
			properties.put(DocumentProperties.DOCUMENT_AUTHOR, 
					   new PropertyDef(DocumentProperties.DOCUMENT_AUTHOR,getMetaData().getAuthor()));
			properties.put(DocumentProperties.DOCUMENT_URL, 
					   new PropertyDef(DocumentProperties.DOCUMENT_URL,getMetaData().getUrl()));
			properties.addProperty(DocumentProperties.DOCUMENT_UPDATE_DATE,getDate());
			properties.addProperty(DocumentProperties.DOCUMENT_PARENT,getParent());
			properties.addProperty(DocumentProperties.DOCUMENT_PATH,getPath());
			properties.addProperty(DocumentProperties.DOCUMENT_CREATOR,getCreator());
			properties.addProperty(DocumentProperties.DOCUMENT_ID,getId());
			properties.addProperty(DocumentProperties.DOCUMENT_TYPECODE,getTypecode());
			properties.addProperty(DocumentProperties.DOCUMENT_POSITION,getPosition());
			
			//Compatibility set
			if (getMetaData().getLanguage() == null) {
				getMetaData().setLanguage(DocumentMetaData.UNKNOWN_LANGUAGE);
			}
			properties.addProperty(DocumentProperties.DOCUMENT_LANGUAGE,getMetaData().getLanguage());
			
			if (getMetaData().getDate() == null) {
				properties.addProperty(DocumentProperties.DOCUMENT_CREATION_DATE,new Date()); 
			} else {
				properties.addProperty(DocumentProperties.DOCUMENT_CREATION_DATE,getMetaData().getDate()); 				
			}
			
			// Add custom properties
			if (customProperties != null) {
				Iterator it = customProperties.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry)it.next();
					properties.addCustomProperty(entry.getKey(), entry.getValue());
				}
			}
			
			Iterator it = getInsertedNotes().iterator();
			while (it.hasNext()) {
				Note note = (Note)it.next();
				if (note == null) {
					continue;
				}

				properties.put(DocumentProperties.DOCUMENT_ADD_NOTE, 
						   new PropertyDef(DocumentProperties.DOCUMENT_ADD_NOTE,note));
				
			}

			it = getUpdatedNotes().iterator();
			while (it.hasNext()) {
				Note note = (Note)it.next();
				if (note == null) {
					continue;
				}
				properties.put(DocumentProperties.DOCUMENT_UPDATE_NOTE, 
						   new PropertyDef(DocumentProperties.DOCUMENT_UPDATE_NOTE,note));				
			}
			
			
			it = getDeletedNotes().iterator();
			while (it.hasNext()) {
				Note note = (Note)it.next();
				if (note == null) {
					continue;
				}				
				properties.put(DocumentProperties.DOCUMENT_DELETE_NOTE, 
						   new PropertyDef(DocumentProperties.DOCUMENT_DELETE_NOTE,note));
				
			}

			it = getInsertedRelations().iterator();
			while (it.hasNext()) {
				Relation relation = (Relation)it.next();
				if (relation == null) {
					continue;
				}
				
				properties.put(DocumentProperties.DOCUMENT_ADD_RELATION, 
						   new PropertyDef(DocumentProperties.DOCUMENT_ADD_RELATION,relation));
				
			}

			it = getDeletedRelations().iterator();
			while (it.hasNext()) {
				Relation relation = (Relation)it.next();
				if (relation == null) {
					continue;
				}				
				properties.put(DocumentProperties.DOCUMENT_DELETE_RELATION, 
						   new PropertyDef(DocumentProperties.DOCUMENT_DELETE_RELATION,relation));
				
			}
			if (insertedCategories != null) {
				it = insertedCategories.iterator();
				while (it.hasNext()) {
					Category category = (Category)it.next();
					if (category == null) {
						continue;
					}
	
					properties.put(DocumentProperties.DOCUMENT_ADD_CATEGORY, 
							   new PropertyDef(DocumentProperties.DOCUMENT_ADD_CATEGORY,category.getId()));
					
				}
			}
			if (deletedCategories != null) {
				it = deletedCategories.iterator();
				while (it.hasNext()) {
					Category category = (Category)it.next();
					if (category == null) {
						continue;
					}				
					properties.put(DocumentProperties.DOCUMENT_DELETE_CATEGORY, 
							   new PropertyDef(DocumentProperties.DOCUMENT_DELETE_CATEGORY,category.getId()));
					
				}
			}			
			/*
			 * At this time, this is done in client SaveAction 
			 * 
			it = getInsertedResources().iterator();
			ResourceProperties[] resProperties = new ResourceProperties[getInsertedResources().size()];
			int i = 0;
			while (it.hasNext()) {
				Resource resource = (Resource)it.next();
				if (resource == null) {
					continue;
				}
				resProperties[i++] = resource.dumpProperties();			  
			}
			properties.put(DocumentProperties.DOCUMENT_ADD_RESOURCE, 
						   new PropertyDef(DocumentProperties.DOCUMENT_ADD_RESOURCE,resProperties));
			
			it = getDeletedResources().iterator();
			resProperties = new ResourceProperties[getDeletedResources().size()];
			while (it.hasNext()) {
				Resource resource = (Resource)it.next();
				if (resource == null) {
					continue;
				}
				resProperties[i++] = resource.dumpProperties();	
			}
			properties.put(DocumentProperties.DOCUMENT_DELETE_RESOURCE, 
						   new PropertyDef(DocumentProperties.DOCUMENT_DELETE_RESOURCE,resProperties));
						   
						   
						   
			clearInsertedResources();
			clearDeletedResources();
			*/			
			
		} catch (InvalidPropertyTypeException e) {
			logger.error(e.getMessage(),e);
		} catch (PropertyNotFoundException e) {
			logger.error(e.getMessage(),e);
		}
		
		clearDeletedNotes();
		clearDeletedRelations();
		clearInsertedNotes();
		clearInsertedRelations();
		clearInsertedCategories();
		clearDeletedCategories();
		
		return properties;
	}
	
	public Set getResourceNodes() {
		return resourceNodes;
	}

	public void setResourceNodes(Set resourceNodes) {
		this.resourceNodes = resourceNodes;
	}	
	
    public String getLastVersionId() {
		return lastVersionId;
	}

	public void setLastVersionId(String lastVersionId) {
		this.lastVersionId = lastVersionId;
	}

	public boolean hasVersions() {
		return (lastVersionId != getId());
	}
	
	public Object getProperty(Object key) {
		
		if (customProperties == null) {
			return null;
		} else {
			return customProperties.get(key);
		}
	}
	
	public void putProperty(Object key, Object value) {
		
		if (customProperties == null) {
			customProperties = new HashMap();
		}
		customProperties.put(key, value);
	}

	public Map getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(Map customProperties) {
		this.customProperties = customProperties;
	}	
	
	public boolean isImage() {
		
		if (getTypecode() == null) {
			return false;
		}
		return org.jlibrary.core.entities.Types.isImageFile(getTypecode());
	}
}
