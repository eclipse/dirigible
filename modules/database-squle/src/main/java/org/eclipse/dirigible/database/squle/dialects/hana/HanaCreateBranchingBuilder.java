package org.eclipse.dirigible.database.squle.dialects.hana;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.table.CreateTableBuilder;

public class HanaCreateBranchingBuilder extends CreateBranchingBuilder {

	protected HanaCreateBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	@Override
	public CreateTableBuilder table(String table, Object...args) {
		if (args != null && args.length >= 1 && args[0] instanceof Boolean) {
			return new HanaCreateTableBuilder(this.getDialect(), table, (Boolean) args[0]);
		}
		return new HanaCreateTableBuilder(this.getDialect(), table, false);
	}

}
