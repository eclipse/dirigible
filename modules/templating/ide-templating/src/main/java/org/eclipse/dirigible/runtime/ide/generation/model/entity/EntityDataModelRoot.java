/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.ide.generation.model.entity;

import java.util.List;

/**
 * Transport object for the Entity Data Model
 *
 */
public class EntityDataModelRoot {
	
	private List<EntityDataModelEntity> entities;
	
	private List<EntityDataModelPerspective> perspectives;
	
	private List<EntityDataModelSidebarItem> sidebar;

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

	/**
	 * @return the perspectives
	 */
	public List<EntityDataModelPerspective> getPerspectives() {
		return perspectives;
	}

	/**
	 * @param perspectives the perspectives to set
	 */
	public void setPerspectives(List<EntityDataModelPerspective> perspectives) {
		this.perspectives = perspectives;
	}

	/**
	 * @return the sidebar
	 */
	public List<EntityDataModelSidebarItem> getSidebar() {
		return sidebar;
	}

	/**
	 * @param sidebar the sidebar to set
	 */
	public void setSidebar(List<EntityDataModelSidebarItem> sidebar) {
		this.sidebar = sidebar;
	}

}
