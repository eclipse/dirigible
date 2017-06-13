package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;

public abstract class AbstractDropSquleBuilder extends AbstractSquleBuilder {

	protected AbstractDropSquleBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	protected void generateDrop(StringBuilder sql) {
		sql.append(KEYWORD_DROP);
	}

}
