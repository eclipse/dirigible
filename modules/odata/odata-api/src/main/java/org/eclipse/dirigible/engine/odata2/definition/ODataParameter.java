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

public class ODataParameter {

    private String name;

    private String column;

    private boolean nullable;

    private String type;

	public String getName() {
		return name;
	}

	public ODataParameter setName(String name) {
		this.name = name;
		return this;
	}

	public String getColumn() {
		return column;
	}

	public ODataParameter setColumn(String column) {
		this.column = column;
		return this;
	}

	public boolean isNullable() {
		return nullable;
	}

	public ODataParameter setNullable(boolean nullable) {
		this.nullable = nullable;
		return this;
	}

	public String getType() {
		return type;
	}

	public ODataParameter setType(String type) {
		this.type = type;
		return this;
	}
    
}
