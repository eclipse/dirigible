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

import javax.persistence.Transient;

/**
 * The view model representation.
 */
public class DataStructureViewModel extends DataStructureModel {

	@Transient
	private String query;

	/**
	 * Getter for the query field.
	 *
	 * @return the SQL query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Setter for the query field.
	 *
	 * @param query            the SQL query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

}
