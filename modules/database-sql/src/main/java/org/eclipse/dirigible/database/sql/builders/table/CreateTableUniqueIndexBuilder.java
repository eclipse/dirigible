package org.eclipse.dirigible.database.sql.builders.table;

import org.eclipse.dirigible.database.sql.ISqlDialect;

public class CreateTableUniqueIndexBuilder extends AbstractCreateTableConstraintBuilder<CreateTableUniqueIndexBuilder> {

	CreateTableUniqueIndexBuilder(ISqlDialect dialect, String name) {
		super(dialect, name);
	}

}
