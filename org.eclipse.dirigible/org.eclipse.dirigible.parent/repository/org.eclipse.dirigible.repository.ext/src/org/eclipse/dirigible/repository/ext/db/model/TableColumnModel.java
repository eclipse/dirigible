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
	 *            the name
	 * @param type
	 *            the type
	 * @param length
	 *            the length
	 * @param notNull
	 *            whether null values are allowed
	 * @param primaryKey
	 *            whether it is a primary key
	 * @param defaultValue
	 *            the default value
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

	/**
	 * Getter for the name
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name
	 *
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the type
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter for the type
	 *
	 * @param type
	 *            the type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Getter for the length
	 *
	 * @return the length
	 */
	public String getLength() {
		return length;
	}

	/**
	 * Setter for the length
	 *
	 * @param length
	 *            the length
	 */
	public void setLength(String length) {
		this.length = length;
	}

	/**
	 * Check for notNull
	 *
	 * @return true if not null
	 */
	public boolean isNotNull() {
		return notNull;
	}

	/**
	 * Setter for the notNull
	 *
	 * @param notNull
	 *            whether null values are allowed
	 */
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	/**
	 * Check for primary key
	 *
	 * @return true if primary key
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * Setter for the primary key
	 *
	 * @param primaryKey
	 *            whether it is a primary key
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * Getter for the default value
	 *
	 * @return the default value
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Setter for the default value
	 *
	 * @param defaultValue
	 *            the default value
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

}
