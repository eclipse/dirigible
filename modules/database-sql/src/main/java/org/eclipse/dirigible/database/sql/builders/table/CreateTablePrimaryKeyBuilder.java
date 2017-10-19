package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class CreateTablePrimaryKeyBuilder extends AbstractCreateTableConstraintBuilder<CreateTablePrimaryKeyBuilder> {

	CreateTablePrimaryKeyBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

}
