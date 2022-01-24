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
