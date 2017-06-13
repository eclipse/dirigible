package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;

public abstract class AbstractCreateSquleBuilder extends AbstractSquleBuilder {

	protected AbstractCreateSquleBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	protected void generateCreate(StringBuilder sql) {
		sql.append(KEYWORD_CREATE);
	}

}
