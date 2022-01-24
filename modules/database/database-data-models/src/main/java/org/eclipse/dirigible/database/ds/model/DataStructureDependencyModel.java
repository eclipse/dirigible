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
package org.eclipse.dirigible.database.ds.model;

/**
 * Dependency element of the DataStructureModel.
 */
public class DataStructureDependencyModel {

	private String name;

	private String type;

	/**
	 * The constructor from fields.
	 *
	 * @param name            the name
	 * @param type            the type
	 */
	public DataStructureDependencyModel(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	/**
	 * Getter for the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the name.
	 *
	 * @param name            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter for the type.
	 *
	 * @param type            the type
	 */
	public void setType(String type) {
		this.type = type;
	}

}
