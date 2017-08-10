/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.builders.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractCreateSquleBuilder;

public class CreateViewBuilder extends AbstractCreateSquleBuilder {

	private String view = null;
	private List<String> columns = new ArrayList<String>();
	private String select = null;

	public CreateViewBuilder(ISquleDialect dialect, String view) {
		super(dialect);
		this.view = view;
	}

	protected String getView() {
		return view;
	}

	protected List<String> getColumns() {
		return columns;
	}

	protected String getSelect() {
		return select;
	}

	protected void setSelect(String select) {
		this.select = select;
	}

	public CreateViewBuilder column(String name) {
		this.columns.add(name);
		return this;
	}

	public CreateViewBuilder asSelect(String select) {
		this.select = select;
		return this;
	}

	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// CREATE
		generateCreate(sql);

		// VIEW
		generateView(sql);

		// COLUMNS
		generateColumns(sql);

		// SELECT
		generateAsSelect(sql);

		return sql.toString();
	}

	protected void generateView(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_VIEW).append(SPACE).append(this.view);
	}

	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(SPACE).append(OPEN).append(traverseColumns()).append(CLOSE);
		}
	}

	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		snippet.append(SPACE);
		for (String column : this.columns) {
			snippet.append(column).append(SPACE);
			snippet.append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	protected void generateAsSelect(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_AS).append(SPACE).append(this.select);
	}

}
