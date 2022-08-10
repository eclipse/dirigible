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
package org.eclipse.dirigible.database.ds.model.transfer;

/**
 * The Class TableColumn.
 */
public class TableColumn {

	/** The name. */
	private String name;

	/** The type. */
	private int type;

	/** The key. */
	private boolean key;

	/**
	 * Instantiates a new table column.
	 *
	 * @param name the name
	 * @param type the type
	 * @param key the key
	 * @param visible the visible
	 */
	public TableColumn(String name, int type, boolean key, boolean visible) {
		super();
		this.name = name;
		this.type = type;
		this.key = key;
	}

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
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Checks if is key.
	 *
	 * @return true, if is key
	 */
	public boolean isKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(boolean key) {
		this.key = key;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(int type) {
		this.type = type;
	}

}