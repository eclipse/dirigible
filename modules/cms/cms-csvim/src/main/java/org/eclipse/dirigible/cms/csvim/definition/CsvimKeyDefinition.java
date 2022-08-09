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
package org.eclipse.dirigible.cms.csvim.definition;

import java.util.List;

/**
 * The Class CsvimKeyDefinition.
 */
public class CsvimKeyDefinition {
	
	/** The column. */
	private String column;
	
	/** The values. */
	private List<String> values;

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
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Sets the values.
	 *
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

}
