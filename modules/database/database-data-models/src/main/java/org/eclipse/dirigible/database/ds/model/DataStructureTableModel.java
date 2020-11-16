/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.database.ds.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The table model representation.
 */
public class DataStructureTableModel extends DataStructureModel {

	private List<DataStructureTableColumnModel> columns = new ArrayList<DataStructureTableColumnModel>();

	private DataStructureTableConstraintsModel constraints = new DataStructureTableConstraintsModel();

	/**
	 * Getter for the columns.
	 *
	 * @return the columns
	 */
	public List<DataStructureTableColumnModel> getColumns() {
		return columns;
	}

	/**
	 * Gets the constraints.
	 *
	 * @return the constraints
	 */
	public DataStructureTableConstraintsModel getConstraints() {
		return constraints;
	}

}
