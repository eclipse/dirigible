/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.builders;

import java.util.List;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public abstract class AbstractQuerySqlBuilder extends AbstractSqlBuilder {

	protected AbstractQuerySqlBuilder(ISqlDialect dialect) {
		super(dialect);
	}

	protected void generateCreate(StringBuilder sql) {
		sql.append(KEYWORD_CREATE);
	}

	protected void generateWhere(StringBuilder sql, List<String> wheres) {
		if (!wheres.isEmpty()) {
			sql.append(SPACE).append(KEYWORD_WHERE).append(SPACE).append(traverseWheres(wheres));
		}
	}

	protected void generateOrderBy(StringBuilder sql, List<String> orders) {
		if (!orders.isEmpty()) {
			sql.append(SPACE).append(KEYWORD_ORDER_BY).append(SPACE).append(traverseOrders(orders));
		}
	}

	protected void generateLimitAndOffset(StringBuilder sql, int limit, int offset) {
		if (limit > -1) {
			sql.append(SPACE).append(KEYWORD_LIMIT).append(SPACE).append(limit);
		}
		if (offset > -1) {
			sql.append(SPACE).append(KEYWORD_OFFSET).append(SPACE).append(offset);
		}
	}

	private String traverseWheres(List<String> wheres) {
		StringBuilder snippet = new StringBuilder();
		for (String where : wheres) {
			snippet.append(where).append(SPACE).append(KEYWORD_AND).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 5);
	}

	private String traverseOrders(List<String> orders) {
		StringBuilder snippet = new StringBuilder();
		for (String order : orders) {
			snippet.append(order).append(COMMA).append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	@Override
	public String toString() {
		return generate();
	}

}
