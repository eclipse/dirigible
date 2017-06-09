package org.eclipse.dirigible.database.squle;

import java.util.List;

public abstract class AbstractSquleBuilder implements ISquleBuilder {

	protected void generateSelect(StringBuilder sql) {
		sql.append(KEYWORD_SELECT);
	}

	protected void generateInsert(StringBuilder sql) {
		sql.append(KEYWORD_INSERT);
	}

	protected void generateUpdate(StringBuilder sql) {
		sql.append(KEYWORD_UPDATE);
	}

	protected void generateWhere(StringBuilder sql, List<String> wheres) {
		if (!wheres.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_WHERE)
				.append(SPACE)
				.append(traverseWheres(wheres));
		}
	}

	protected void generateOrderBy(StringBuilder sql, List<String> orders) {
		if (!orders.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_ORDER_BY)
				.append(SPACE)
				.append(traverseOrders(orders));
		}
	}

	protected void generateLimit(StringBuilder sql, int limit) {
		if (limit > -1) {
			sql.append(SPACE)
				.append(KEYWORD_LIMIT)
				.append(SPACE)
				.append(limit);
		}
	}

	private String traverseWheres(List<String> wheres) {
		StringBuilder snippet = new StringBuilder();
		for (String where : wheres) {
			snippet.append(where)
				.append(SPACE)
				.append(KEYWORD_AND)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 5);
	}

	private String traverseOrders(List<String> orders) {
		StringBuilder snippet = new StringBuilder();
		for (String order : orders) {
			snippet.append(order)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

	@Override
	public String toString() {
		return generate();
	}

}
