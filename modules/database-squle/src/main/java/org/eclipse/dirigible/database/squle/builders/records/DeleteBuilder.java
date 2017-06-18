package org.eclipse.dirigible.database.squle.builders.records;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractQuerySquleBuilder;

public class DeleteBuilder extends AbstractQuerySquleBuilder {

	private String table;
	private List<String> wheres = new ArrayList<String>();

	public DeleteBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	public DeleteBuilder from(String table) {
		this.table = table;
		return this;
	}

	public DeleteBuilder where(String condition) {
		wheres.add(OPEN + condition + CLOSE);
		return this;
	}

	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();

		// UPDATE
		generateDelete(sql);

		// TABLE
		generateTable(sql);

		// WHERE
		generateWhere(sql, wheres);
	
		return sql.toString();
	}

	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_FROM)
			.append(SPACE)
			.append(this.table);
	}
	
	protected void generateDelete(StringBuilder sql) {
		sql.append(KEYWORD_DELETE);
	}

	public String getTable() {
		return table;
	}

	public List<String> getWheres() {
		return wheres;
	}
	
	
}
