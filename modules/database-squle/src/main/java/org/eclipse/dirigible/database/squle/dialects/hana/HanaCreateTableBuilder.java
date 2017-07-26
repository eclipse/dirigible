package org.eclipse.dirigible.database.squle.dialects.hana;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.table.CreateTableBuilder;

public class HanaCreateTableBuilder extends CreateTableBuilder {
	
	private boolean isColumnTable;

	public HanaCreateTableBuilder(ISquleDialect dialect, String table, boolean isColumnTable) {
		super(dialect, table);
		this.isColumnTable = isColumnTable;
	}
	
	@Override
	protected void generateTable(StringBuilder sql) {
		sql.append(SPACE)
			.append(isColumnTable ? KEYWORD_COLUMN : "")
			.append(isColumnTable ? SPACE : "")
			.append(KEYWORD_TABLE)
			.append(SPACE)
			.append(getTable());
	}

}
