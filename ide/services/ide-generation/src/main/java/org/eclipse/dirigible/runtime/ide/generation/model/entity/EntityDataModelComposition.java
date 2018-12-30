/**
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.ide.generation.model.entity;

public class EntityDataModelComposition {
	
	private String entityName;
	private String entityProperty;
	private String localProperty;
	
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}
	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	/**
	 * @return the entityProperty
	 */
	public String getEntityProperty() {
		return entityProperty;
	}
	/**
	 * @param entityProperty the entityProperty to set
	 */
	public void setEntityProperty(String entityProperty) {
		this.entityProperty = entityProperty;
	}
	/**
	 * @return the localProperty
	 */
	public String getLocalProperty() {
		return localProperty;
	}
	/**
	 * @param localProperty the localProperty to set
	 */
	public void setLocalProperty(String localProperty) {
		this.localProperty = localProperty;
	}
	
}
