/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.model;

/**
 * The column element of the table model
 */
public class TableColumnModel {

	private String name;

	private String type;

	private String length;

	private boolean notNull;

	private boolean primaryKey;

	private String defaultValue;

	/**
	 * The constructor from the fields
	 * 
	 * @param name
	 * @param type
	 * @param length
	 * @param notNull
	 * @param primaryKey
	 * @param defaultValue
	 */
	public TableColumnModel(String name, String type, String length, boolean notNull, boolean primaryKey, String defaultValue) {
		super();
		this.name = name;
		this.type = type;
		this.length = length;
		this.notNull = notNull;
		this.primaryKey = primaryKey;
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
