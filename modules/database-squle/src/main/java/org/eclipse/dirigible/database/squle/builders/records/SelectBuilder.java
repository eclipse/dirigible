package org.eclipse.dirigible.database.squle.builders.records;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractQuerySquleBuilder;

public class SelectBuilder extends AbstractQuerySquleBuilder {
	
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
	
	public SelectBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	public SelectBuilder distinct() {
		this.distinct = true;
		return this;
	}
	
	public SelectBuilder column(String column) {
		this.columns.add(column);
		return this;
	}
	
	public SelectBuilder from(String table) {
		return from(table, null);
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
		return join(table, on, null);
	}
	
	public SelectBuilder join(String table, String on, String alias) {
		return genericJoin(KEYWORD_INNER, table, on, alias);
	}
	
	public SelectBuilder innerJoin(String table, String on) {
		return innerJoin(table, on, null);
	}
	
	public SelectBuilder innerJoin(String table, String on, String alias) {
		return genericJoin(KEYWORD_INNER, table, on, alias);
	}
	
	public SelectBuilder outerJoin(String table, String on) {
		return outerJoin(table, on, null);
	}
	
	public SelectBuilder outerJoin(String table, String on, String alias) {
		return genericJoin(KEYWORD_OUTER, table, on, alias);
	}
	
	public SelectBuilder leftJoin(String table, String on) {
		return leftJoin(table, on, null);
	}
	
	public SelectBuilder leftJoin(String table, String on, String alias) {
		return genericJoin(KEYWORD_LEFT, table, on, alias);
	}
	
	public SelectBuilder rightJoin(String table, String on) {
		return rightJoin(table, on, null);
	}
	
	public SelectBuilder rightJoin(String table, String on, String alias) {
		return genericJoin(KEYWORD_RIGHT, table, on, alias);
	}
	
	public SelectBuilder fullJoin(String table, String on) {
		return fullJoin(table, on, null);
	}
	
	public SelectBuilder fullJoin(String table, String on, String alias) {
		return genericJoin(KEYWORD_FULL, table, on, alias);
	}
	
	public SelectBuilder genericJoin(String type, String table, String on, String alias) {
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
		return this;
	}
	
	public SelectBuilder where(String condition) {
		this.wheres.add(OPEN + condition + CLOSE);
		return this;
	}
	
	public SelectBuilder order(String column) {
		return order(column, true);
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
		generateWhere(sql, wheres);
		
		// GROUP BY
		generateGroupBy(sql);
		
		// HAVING
		generateHaving(sql);
		
		// ORDER BY
		generateOrderBy(sql, orders);
		
		// LIMIT
		generateLimit(sql, limit);
		
		// OFFSET
		generateOffset(sql);
		
		// UNION
		generateUnion(sql);
		
		return sql.toString();
	}

	protected void generateUnion(StringBuilder sql) {
		if (!unions.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_UNION)
				.append(SPACE)
				.append(traverseUnions());
		}
	}

	protected void generateOffset(StringBuilder sql) {
		if (offset > -1) {
			sql.append(SPACE)
				.append(KEYWORD_OFFSET)
				.append(SPACE)
				.append(offset);
		}
	}

	protected void generateHaving(StringBuilder sql) {
		if (having != null) {
			sql.append(SPACE)
			.append(KEYWORD_HAVING)
			.append(SPACE)
			.append(this.having);
		}
	}

	protected void generateGroupBy(StringBuilder sql) {
		if (!groups.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_GROUP_BY)
				.append(SPACE)
				.append(traverseGroups());
		}
	}

	protected void generateJoins(StringBuilder sql) {
		if (!joins.isEmpty()) {
			sql.append(SPACE)
				.append(traverseJoins());
		}
	}

	protected void generateTables(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_FROM)
			.append(SPACE)
			.append(traverseTables());
	}

	protected void generateColumns(StringBuilder sql) {
		sql.append(SPACE)
			.append(traverseColumns());
	}

	protected void generateDistinct(StringBuilder sql) {
		if (distinct) {
			sql.append(SPACE)
			.append(KEYWORD_DISTINCT);
		}
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
	
	protected void generateSelect(StringBuilder sql) {
		sql.append(KEYWORD_SELECT);
	}

	public List<String> getColumns() {
		return columns;
	}

	public List<String> getTables() {
		return tables;
	}

	public List<String> getJoins() {
		return joins;
	}

	public List<String> getWheres() {
		return wheres;
	}

	public List<String> getOrders() {
		return orders;
	}

	public List<String> getGroups() {
		return groups;
	}

	public List<String> getUnions() {
		return unions;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public String getHaving() {
		return having;
	}

	public int getLimit() {
		return limit;
	}

	public int getOffset() {
		return offset;
	}
	
	
}
