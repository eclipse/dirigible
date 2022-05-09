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
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.ArrayList;
import java.util.List;

public class ODataAssociationEndDefinition {

    private String entity;

    private String property;
    
    private List<String> properties = new ArrayList<String>();

    private String column;

    private String multiplicity;

	private ODataManyToManyMappingTableDefinition mappingTableDefinition = new ODataManyToManyMappingTableDefinition();

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public List<String> getProperties() {
		return properties;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(String multiplicity) {
		this.multiplicity = multiplicity;
	}

	public ODataManyToManyMappingTableDefinition getMappingTableDefinition() {
		return mappingTableDefinition;
	}

	public void setMappingTableDefinition(ODataManyToManyMappingTableDefinition mappingTableDefinition) {
		this.mappingTableDefinition = mappingTableDefinition;
	}
}
