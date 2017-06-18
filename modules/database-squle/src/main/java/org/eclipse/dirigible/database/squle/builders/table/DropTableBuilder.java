package org.eclipse.dirigible.database.squle.builders.table;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractDropSquleBuilder;

public class DropTableBuilder extends AbstractDropSquleBuilder {
	
	private String table = null;
	
	public DropTableBuilder(ISquleDialect dialect, String table) {
		super(dialect);
		this.table = table;
	}

	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// DROP
		generateDrop(sql);
		
		// TABLE
		generateTable(sql);
		
		return sql.toString();
	}
	
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_TABLE)
			.append(SPACE)
			.append(this.table);
	}
	
}
