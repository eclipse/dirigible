/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.odata2.definition;

import java.util.HashMap;
import java.util.Map;

public class ODataProperty {

    private String name;

    private String column;

    private boolean nullable;

    private String type;

    /**
     * <p>Define list of additional annotations for Property element.</p>
     * For example:
     * <code> &lt;Property Name="SomeName" Type="Edm.String" sap:label="SomeLabel" sap:filterable="false"/&gt; </code>
     */
    private Map<String ,String> annotationsProperty = new HashMap<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getAnnotationsProperty() {
		return annotationsProperty;
	}

	public void setAnnotationsProperty(Map<String, String> annotationsProperty) {
		this.annotationsProperty = annotationsProperty;
	}
    
    
}
