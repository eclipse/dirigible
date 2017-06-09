package org.eclipse.dirigible.database.squle;

import java.util.ArrayList;
import java.util.List;

public class InsertBuilder extends AbstractSquleBuilder {
	
	private String table = null;
	private List<String> columns = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	private String select = null;

	public InsertBuilder into(String table) {
		this.table = table;
		return this;
	}
	
	public InsertBuilder column(String name) {
		this.columns.add(name);
		return this;
	}
	
	public InsertBuilder value(String value) {
		this.values.add(value);
		return this;
	}
	
	public InsertBuilder select(String select) {
		this.select = select;
		return this;
	}
	
	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// INSERT
		generateInsert(sql);
		
		// TABLE
		generateTable(sql);
		
		// COLUMNS
		generateColumns(sql);
		
		// VALUES
		generateValues(sql);
		
		// SELECT
		generateSelect(sql);
		
		return sql.toString();
	}
	
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_INTO)
			.append(SPACE)
			.append(this.table);
	}
	
	protected void generateColumns(StringBuilder sql) {
		if (!this.columns.isEmpty()) {
			sql.append(SPACE)
				.append(OPEN)
				.append(traverseColumns())
				.append(CLOSE);
		}
	}
	
	protected void generateValues(StringBuilder sql) {
		if (!this.values.isEmpty()) {
			sql.append(SPACE)
				.append(KEYWORD_VALUES)
				.append(SPACE)
				.append(OPEN)
				.append(traverseValues())
				.append(CLOSE);
		} else if (!this.columns.isEmpty() && this.select == null){
			sql.append(SPACE)
				.append(KEYWORD_VALUES)
				.append(SPACE)
				.append(OPEN)
				.append(enumerateValues())
				.append(CLOSE);
		}
	}
	
	protected void generateSelect(StringBuilder sql) {
		if (this.select != null) {
			sql.append(SPACE)
				.append(this.select);
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
	
	protected String traverseValues() {
		StringBuilder snippet = new StringBuilder();
		for (String value : this.values) {
			snippet.append(value)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}
	
	protected String enumerateValues() {
		StringBuilder snippet = new StringBuilder();
		for (int i=0; i< columns.size(); i++) {
			snippet.append(QUESTION)
				.append(COMMA)
				.append(SPACE);
		}
		return snippet.toString().substring(0, snippet.length() - 2);
	}

}
