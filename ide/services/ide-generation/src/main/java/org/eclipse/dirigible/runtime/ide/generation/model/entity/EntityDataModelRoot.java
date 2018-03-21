package org.eclipse.dirigible.runtime.ide.generation.model.entity;

import java.util.List;

/**
 * Transport object for the Entity Data Model
 *
 */
public class EntityDataModelRoot {
	
	private List<EntityDataModelEntity> entities;

	/**
	 * Gets the entities
	 * 
	 * @return the entities
	 */
	public List<EntityDataModelEntity> getEntities() {
		return entities;
	}

	/**
	 * Sets the entities
	 * 
	 * @param entities the entities to set
	 */
	public void setEntities(List<EntityDataModelEntity> entities) {
		this.entities = entities;
	}

}
