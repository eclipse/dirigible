package org.eclipse.dirigible.database.sql.builders.table;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class CreateTableForeignKeyBuilder extends AbstractCreateTableConstraintBuilder<CreateTableForeignKeyBuilder> {

	private String referencedTable;
	private Set<String> referencedColumns = new TreeSet<String>();

	CreateTableForeignKeyBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

	public String getReferencedTable() {
		return referencedTable;
	}

	public Set<String> getReferencedColumns() {
		return referencedColumns;
	}

	public CreateTableForeignKeyBuilder referencedTable(String referencedTable) {
		this.referencedTable = referencedTable;
		return this;
	}

	public CreateTableForeignKeyBuilder referencedColumn(String referencedColumn) {
		this.referencedColumns.add(referencedColumn);
		return this;
	}

}
