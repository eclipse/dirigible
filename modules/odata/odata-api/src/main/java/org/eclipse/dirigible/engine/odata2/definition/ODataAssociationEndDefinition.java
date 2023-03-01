/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class ODataAssociationEndDefinition.
 */
public class ODataAssociationEndDefinition {

    /** The entity. */
    private String entity;

    /** The property. */
    private String property;
    
    /** The properties. */
    private List<String> properties = new ArrayList<String>();

    /** The column. */
    private String column;

    /** The multiplicity. */
    private String multiplicity;

	/** The mapping table definition. */
	private ODataManyToManyMappingTableDefinition mappingTableDefinition = new ODataManyToManyMappingTableDefinition();

	/**
	 * Gets the entity.
	 *
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Sets the entity.
	 *
	 * @param entity the new entity
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Sets the property.
	 *
	 * @param property the new property
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	public List<String> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties.
	 *
	 * @param properties the new properties
	 */
	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	/**
	 * Gets the column.
	 *
	 * @return the column
	 */
	public String getColumn() {
		return column;
	}

	/**
	 * Sets the column.
	 *
	 * @param column the new column
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * Gets the multiplicity.
	 *
	 * @return the multiplicity
	 */
	public String getMultiplicity() {
		return multiplicity;
	}

	/**
	 * Sets the multiplicity.
	 *
	 * @param multiplicity the new multiplicity
	 */
	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	/**
	 * Gets the mapping table definition.
	 *
	 * @return the mapping table definition
	 */
	public ODataManyToManyMappingTableDefinition getMappingTableDefinition() {
		return mappingTableDefinition;
	}

	/**
	 * Sets the mapping table definition.
	 *
	 * @param mappingTableDefinition the new mapping table definition
	 */
	public void setMappingTableDefinition(ODataManyToManyMappingTableDefinition mappingTableDefinition) {
		this.mappingTableDefinition = mappingTableDefinition;
	}
}
