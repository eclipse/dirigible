/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.ds.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The table model representation
 * {
 * "name": "CUSTOMERS",
 * "type": "TABLE",
 * "columns":
 * [
 * {
 * "name":"id",
 * "type":"INTEGER",
 * "length":"0",
 * "notNull":"true",
 * "primaryKey":"true",
 * "defaultValue":""
 * },
 * {
 * "name":"CUSTOMER_FIRST_NAME",
 * "type":"VARCHAR",
 * "length":"32",
 * "notNull":"false",
 * "primaryKey":"false",
 * "defaultValue":""
 * },
 * {
 * "name":"CUSTOMER_BALANCE",
 * "type":"VARCHAR",
 * "length":"32",
 * "notNull":"false",
 * "primaryKey":"false",
 * "defaultValue":"",
 * "precision": "5",
 * "scale": "2"
 * }
 * ],
 * "dependencies":
 * [
 * {
 * "name":"ADDRESSES",
 * "type":"TABLE"
 * }
 * ]
 * }
 */
public class DataStructureTableModel extends DataStructureModel {

	private List<DataStructureTableColumnModel> columns = new ArrayList<DataStructureTableColumnModel>();

	/**
	 * Getter for the columns
	 *
	 * @return the columns
	 */
	public List<DataStructureTableColumnModel> getColumns() {
		return columns;
	}

}
