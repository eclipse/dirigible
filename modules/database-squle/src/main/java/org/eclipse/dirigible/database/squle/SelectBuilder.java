package org.eclipse.dirigible.database.squle;

import static org.eclipse.dirigible.database.squle.ISquleKeywords.*;
import java.util.ArrayList;
import java.util.List;

public class SelectBuilder extends AbstractSquleBuilder {
	
	private List<String> columns = new ArrayList<String>();
	private List<String> tables = new ArrayList<String>();
	private List<String> joins = new ArrayList<String>();
	private List<String> wheres = new ArrayList<String>();
	private List<String> orders = new ArrayList<String>();
	private List<String> groups = new ArrayList<String>();
	private List<String> unions = new ArrayList<String>();
	private boolean distinct = false;
	private String having = null;
	private int limit = -1;
	private int offset = -1;
	
	
	public SelectBuilder distinct() {
		this.distinct = true;
		return this;
	}
	
	public SelectBuilder column(String name) {
		this.columns.add(name);
		return this;
	}
	
	public SelectBuilder from(String name) {
		from(name, null);
		return this;
	}
	
	public SelectBuilder from(String table, String alias) {
		StringBuilder snippet = new StringBuilder();
		snippet.append(table);
		if (alias != null) {
			snippet.append(SPACE)
			.append(KEYWORD_AS)
			.append(SPACE)
			.append(alias);
		}
		this.tables.add(snippet.toString());
		return this;
	}
	
	public SelectBuilder join(String table, String on) {
		join(table, on, null);
		return this;
	}
	
	public SelectBuilder join(String table, String on, String alias) {
		genericJoin(KEYWORD_INNER, table, on, alias);
		return this;
	}
	
	public SelectBuilder innerJoin(String table, String on) {
		innerJoin(table, on, null);
		return this;
	}
	
	public SelectBuilder innerJoin(String table, String on, String alias) {
		genericJoin(KEYWORD_INNER, table, on, alias);
		return this;
	}
	
	public SelectBuilder outerJoin(String table, String on) {
		outerJoin(table, on, null);
		return this;
	}
	
	public SelectBuilder outerJoin(String table, String on, String alias) {
		genericJoin(KEYWORD_OUTER, table, on, alias);
		return this;
	}
	
	public SelectBuilder leftJoin(String table, String on) {
		leftJoin(table, on, null);
		return this;
	}
	
	public SelectBuilder leftJoin(String table, String on, String alias) {
		genericJoin(KEYWORD_LEFT, table, on, alias);
		return this;
	}
	
	public SelectBuilder rightJoin(String table, String on) {
		rightJoin(table, on, null);
		return this;
	}
	
	public SelectBuilder rightJoin(String table, String on, String alias) {
		genericJoin(KEYWORD_RIGHT, table, on, alias);
		return this;
	}
	
	public SelectBuilder fullJoin(String table, String on) {
		fullJoin(table, on, null);
		return this;
	}
	
	public SelectBuilder fullJoin(String table, String on, String alias) {
		genericJoin(KEYWORD_FULL, table, on, alias);
		return this;
	}
	
	public void genericJoin(String type, String table, String on, String alias) {
		StringBuilder snippet = new StringBuilder();
		snippet.append(type)
			.append(SPACE)
			.append(KEYWORD_JOIN)
			.append(SPACE)
			.append(table)
			.append(SPACE)
			.append(KEYWORD_ON)
			.append(SPACE)
			.append(on);
		
		if (alias != null) {
			snippet.append(SPACE).append(alias);
		}
		this.joins.add(snippet.toString());
	}
	
	public SelectBuilder where(String condition) {
		this.wheres.add(OPEN + condition + CLOSE);
		return this;
	}
	
	public SelectBuilder order(String column) {
		order(column, true);
		return this;
	}
	
	public SelectBuilder order(String column, boolean asc) {
		if (asc) {
			this.orders.add(column + SPACE + KEYWORD_ASC);
		} else {
			this.orders.add(column + SPACE + KEYWORD_DESC);
		}
		
		return this;
	}
	
	public SelectBuilder group(String column) {
		this.groups.add(column);
		return this;
	}
	
	public SelectBuilder limit(int limit) {
		this.limit = limit;
		return this;
	}
	
	public SelectBuilder offset(int offset) {
		this.offset = offset;
		return this;
	}
	
	public SelectBuilder having(String having) {
		this.having = having;
		return this;
	}
	
	public SelectBuilder union(String select) {
		this.unions.add(select);
		return this;
	}
	
	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();
		
		// SELECT
		generateSelect(sql);
		
		//DISTINCT
		generateDistinct(sql);
		
		// COLUMNS
		generateColumns(sql);
		
		// TABLES
		generateTables(sql);
		
		// JOINS
		generateJoins(sql);
		
		// WHERE
		generateWhere(sql);
		
		// GROUP BY
		generateGroupBy(sql);
		
		// HAVING
		generateHaving(sql);
		
		// ORDER BY
		orderBy(sql);
		
		// LIMIT
		generateLimit(sql);
		
		// OFFSET
		generateOffset(sql);
		
		// UNION
		generateUnion(sql);
		
		return sql.toString();
	}

	private void generateUnion(StringBuilder sql) {
		if (!unions.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_UNION)
				.append(SPACE)
				.append(traverseUnions());
		}
	}

	private void generateOffset(StringBuilder sql) {
		if (offset > -1) {
			sql.append(SPACE)
				.append(KEYWORD_OFFSET)
				.append(SPACE)
				.append(offset);
		}
	}

	private void generateLimit(StringBuilder sql) {
		if (limit > -1) {
			sql.append(SPACE)
				.append(KEYWORD_LIMIT)
				.append(SPACE)
				.append(limit);
		}
	}

	private void orderBy(StringBuilder sql) {
		if (!orders.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_ORDER_BY)
				.append(SPACE)
				.append(traverseOrders());
		}
	}

	private void generateHaving(StringBuilder sql) {
		if (having != null) {
			sql.append(SPACE)
			.append(KEYWORD_HAVING)
			.append(SPACE)
			.append(this.having);
		}
	}

	private void generateGroupBy(StringBuilder sql) {
		if (!groups.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_GROUP_BY)
				.append(SPACE)
				.append(traverseGroups());
		}
	}

	private void generateWhere(StringBuilder sql) {
		if (!wheres.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_WHERE)
				.append(SPACE)
				.append(traverseWheres());
		}
	}

	private void generateJoins(StringBuilder sql) {
		if (!joins.isEmpty()) {
			sql.append(SPACE)
				.append(traverseJoins());
		}
	}

	private void generateTables(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_FROM)
			.append(SPACE)
			.append(traverseTables());
	}

	private void generateColumns(StringBuilder sql) {
		sql.append(SPACE)
			.append(traverseColumns());
	}

	private void generateDistinct(StringBuilder sql) {
		if (distinct) {
			sql.append(SPACE)
			.append(KEYWORD_DISTINCT);
		}
	}

	private void generateSelect(StringBuilder sql) {
		sql.append(KEYWORD_SELECT);
	}
	
	@Override
	public String toString() {
		return generate();
	}

	protected String traverseColumns() {
		StringBuilder snippet = new StringBuilder();
		for (String column : this.columns) {
			snippet.append(column)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String traverseTables() {
		StringBuilder snippet = new StringBuilder();
		for (String table : this.tables) {
			snippet.append(table)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String traverseJoins() {
		StringBuilder snippet = new StringBuilder();
		for (String join : this.joins) {
			snippet
				.append(join)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String traverseWheres() {
		StringBuilder snippet = new StringBuilder();
		for (String where : this.wheres) {
			snippet.append(where)
				.append(SPACE)
				.append(KEYWORD_AND)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 5);
	}
	
	protected String traverseOrders() {
		StringBuilder snippet = new StringBuilder();
		for (String order : this.orders) {
			snippet.append(order)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String traverseGroups() {
		StringBuilder snippet = new StringBuilder();
		for (String group : this.groups) {
			snippet.append(group)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String traverseUnions() {
		StringBuilder snippet = new StringBuilder();
		for (String union : this.unions) {
			snippet.append(union)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 1);
	}
}
