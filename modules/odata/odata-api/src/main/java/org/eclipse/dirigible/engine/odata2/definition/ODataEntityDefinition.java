/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.ArrayList;
import java.util.List;

public class ODataEntityDefinition {
	
	private String name;
	
	private String alias;
	
	private String table;
	
	private List<ODataProperty> properties = new ArrayList<ODataProperty>();
	
	private List<ODataNavigation> navigations = new ArrayList<ODataNavigation>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return the properties
	 */
	public List<ODataProperty> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(List<ODataProperty> properties) {
		this.properties = properties;
	}

	/**
	 * @return the navigations
	 */
	public List<ODataNavigation> getNavigations() {
		return navigations;
	}

	/**
	 * @param navigations the navigations to set
	 */
	public void setNavigations(List<ODataNavigation> navigations) {
		this.navigations = navigations;
	}
	
	

}
