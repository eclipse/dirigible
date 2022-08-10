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

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ODataProperty.
 */
public class ODataProperty {

    /** The name. */
    private String name;

    /** The column. */
    private String column;

    /** The nullable. */
    private boolean nullable;

    /** The type. */
    private String type;

    /**
     * <p>Define list of additional annotations for Property element.</p>
     * For example:
     * <code> &lt;Property Name="SomeName" Type="Edm.String" sap:label="SomeLabel" sap:filterable="false"/&gt; </code>
     */
    private Map<String ,String> annotationsProperty = new HashMap<>();

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name
	 * @return the o data property
	 */
	public ODataProperty setName(String name) {
		this.name = name;
		return this;
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
	 * @param column the column
	 * @return the o data property
	 */
	public ODataProperty setColumn(String column) {
		this.column = column;
		return this;
	}

	/**
	 * Checks if is nullable.
	 *
	 * @return true, if is nullable
	 */
	public boolean isNullable() {
		return nullable;
	}

	/**
	 * Sets the nullable.
	 *
	 * @param nullable the nullable
	 * @return the o data property
	 */
	public ODataProperty setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type
	 * @return the o data property
	 */
	public ODataProperty setType(String type) {
		this.type = type;
		return this;
	}

	/**
	 * Gets the annotations property.
	 *
	 * @return the annotations property
	 */
	public Map<String, String> getAnnotationsProperty() {
		return annotationsProperty;
	}

	/**
	 * Sets the annotations property.
	 *
	 * @param annotationsProperty the annotations property
	 */
	public void setAnnotationsProperty(Map<String, String> annotationsProperty) {
		this.annotationsProperty = annotationsProperty;
	}
    
    
}
