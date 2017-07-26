package org.eclipse.dirigible.database.squle.dialects.sybase;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;

public class SybaseSelectBuilder extends SelectBuilder {

	public SybaseSelectBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected void generateLimit(StringBuilder sql, int limit) {
		if (limit > -1) {
			sql.append(SPACE)
				.append(KEYWORD_ROWS)
				.append(SPACE)
				.append(KEYWORD_LIMIT)
				.append(SPACE)
				.append(limit);
		}
	}

}
