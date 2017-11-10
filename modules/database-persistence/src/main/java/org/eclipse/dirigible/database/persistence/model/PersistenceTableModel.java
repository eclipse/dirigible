/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.model;

import java.util.ArrayList;
import java.util.List;

public class PersistenceTableModel {
	
	private String className;
	
	private String tableName;
	
	private String schemaName;
	
	private List<PersistenceTableColumnModel> columns = new ArrayList<PersistenceTableColumnModel>();

	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public List<PersistenceTableColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<PersistenceTableColumnModel> columns) {
		this.columns = columns;
	}
	
}
