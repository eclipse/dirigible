/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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
