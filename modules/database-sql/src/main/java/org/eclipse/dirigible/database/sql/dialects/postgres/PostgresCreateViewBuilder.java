/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.postgres;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.view.CreateViewBuilder;

public class PostgresCreateViewBuilder extends CreateViewBuilder {

	private String values = null;

	public PostgresCreateViewBuilder(ISqlDialect dialect, String view) {
		super(dialect, view);
	}

	@Override
	public PostgresCreateViewBuilder column(String name) {
		super.getColumns().add(name);
		return this;
	}

	@Override
	public PostgresCreateViewBuilder asSelect(String select) {
		if (this.values != null) {
			throw new IllegalStateException("Create VIEW can use either AS SELECT or AS VALUES, but not both.");
		}
		setSelect(select);
		return this;
	}

	public PostgresCreateViewBuilder asValues(String values) {
		if (getSelect() != null) {
			throw new IllegalStateException("Create VIEW can use either AS SELECT or AS VALUES, but not both.");
		}
		this.values = values;
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

		// SELECT or VALUES
		if (getSelect() != null) {
			generateAsSelect(sql);
		} else if (this.values != null) {
			generateAsValues(sql);
		} else {
			throw new IllegalStateException("Create VIEW must use either AS SELECT or AS VALUES.");
		}

		return sql.toString();
	}

	protected void generateAsValues(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_AS).append(SPACE).append(KEYWORD_VALUES).append(SPACE).append(this.values);
	}

}
