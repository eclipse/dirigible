/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.template.ui.db.wizard;


public class DataStructureTemplateModel extends TableTemplateModel {

	private String query;

	private String tableName;

	private String[] dsvSampleRows;
	
	public String getQuery() {
		return query;
	}

	public String getTableName() {
		return tableName;
	}

	public String[] getDsvSampleRows() {
		return dsvSampleRows;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDsvSampleRows(String[] dsvSampleRows) {
		this.dsvSampleRows = dsvSampleRows;
	}

}
