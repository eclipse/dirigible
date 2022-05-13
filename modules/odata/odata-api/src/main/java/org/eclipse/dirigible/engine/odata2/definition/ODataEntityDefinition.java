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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ODataEntityDefinition {

    private String name;

    private String alias;

    private String table;

	private String keyGenerated;

    private List<ODataProperty> properties = new ArrayList<>();

	private List<ODataParameter> parameters = new ArrayList<>();

    private List<ODataNavigation> navigations = new ArrayList<>();

    private List<ODataHandler> handlers = new ArrayList<>();

    /**
     * For VIEW type the keys need to be specified explicitly, because on DB side there will be no keys definitions
     */
    private List<String> keys = new ArrayList<>();

    /**
     * <p>Define list of additional annotations for EntitySet element.</p>
     * For example:
     * <code> &lt;EntitySet Name="SomeName" EntityType="someType" sap:creatable="true" sap:updatable-path="Updatable"&gt; </code>
     */
    private Map<String, String> annotationsEntitySet = new HashMap<>();

    /**
     * <p>Define list of additional annotations for EntityType element.</p>
     * For example:
     * <code> &lt;EntityType Name="SomeTypeName" sap:semantics="aggregate""&gt; </code>
     */
    private Map<String, String> annotationsEntityType = new HashMap<>();

	/**
	 * <p>Define list of aggregation types for the columns.</p>
	 * For example:
	 * <code> &lt;SUM="NUMBER"&gt; </code>
	 */
	private Map<String, String> aggregationsTypeAndColumn = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getKeyGenerated() {
		return keyGenerated;
	}

	public void setKeyGenerated(String keyGenerated) {
		this.keyGenerated = keyGenerated;
	}

	public List<ODataProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ODataProperty> properties) {
		this.properties = properties;
	}

	public List<ODataParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<ODataParameter> parameters) {
		this.parameters = parameters;
	}

	public List<ODataNavigation> getNavigations() {
		return navigations;
	}

	public void setNavigations(List<ODataNavigation> navigations) {
		this.navigations = navigations;
	}

	public List<ODataHandler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<ODataHandler> handlers) {
		this.handlers = handlers;
	}

	public List<String> getKeys() {
		return keys;
	}

	public void setKeys(List<String> keys) {
		this.keys = keys;
	}

	public Map<String, String> getAnnotationsEntitySet() {
		return annotationsEntitySet;
	}

	public void setAnnotationsEntitySet(Map<String, String> annotationsEntitySet) {
		this.annotationsEntitySet = annotationsEntitySet;
	}

	public Map<String, String> getAnnotationsEntityType() {
		return annotationsEntityType;
	}

	public void setAnnotationsEntityType(Map<String, String> annotationsEntityType) {
		this.annotationsEntityType = annotationsEntityType;
	}

	public Map<String, String> getAggregationsTypeAndColumn() {
		return aggregationsTypeAndColumn;
	}

	public void setAggregationsTypeAndColumn(Map<String, String> aggregationsTypeAndColumn) {
		this.aggregationsTypeAndColumn = aggregationsTypeAndColumn;
	}
}
