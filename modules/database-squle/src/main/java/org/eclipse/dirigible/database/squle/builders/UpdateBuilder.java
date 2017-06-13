package org.eclipse.dirigible.database.squle.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.dirigible.database.squle.ISquleDialect;

import java.util.TreeMap;

public class UpdateBuilder extends AbstractQuerySquleBuilder {

	private String table;
	private Map<String, String> values = new TreeMap<String, String>();
	private List<String> wheres = new ArrayList<String>();
	private List<String> orders = new ArrayList<String>();
	private int limit = -1;

	public UpdateBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	public UpdateBuilder table(String table) {
		this.table = table;
		return this;
	}

	public UpdateBuilder set(String column, String value) {
		values.put(column, value);
		return this;
	}

	public UpdateBuilder where(String condition) {
		wheres.add(OPEN + condition + CLOSE);
		return this;
	}

	public UpdateBuilder order(String column) {
		return order(column, true);
	}
	
	public UpdateBuilder order(String column, boolean asc) {
		if (asc) {
			this.orders.add(column + SPACE + KEYWORD_ASC);
		} else {
			this.orders.add(column + SPACE + KEYWORD_DESC);
		}
		return this;
	}

	public UpdateBuilder limit(int limit){
		this.limit = limit;
		return this;
	}

	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();

		// UPDATE
		generateUpdate(sql);

		// TABLE
		generateTable(sql);

		// SET
		generateSetValues(sql);

		// WHERE
		generateWhere(sql, wheres);
		
		// ORDER BY
		generateOrderBy(sql, orders);

		// LIMIT
		generateLimit(sql, limit);

		return sql.toString();
	}

	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(this.table);
	}
	
	protected void generateSetValues(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_SET);
		for (Entry<String, String> next : values.entrySet()) {
			sql.append(SPACE)
				.append(next.getKey())
				.append(SPACE)
				.append(EQUALS)
				.append(SPACE)
				.append(next.getValue())
				.append(COMMA);
		}
		sql.delete(sql.length() - 1, sql.length());
	}
	
	protected void generateUpdate(StringBuilder sql) {
		sql.append(KEYWORD_UPDATE);
	}
}
