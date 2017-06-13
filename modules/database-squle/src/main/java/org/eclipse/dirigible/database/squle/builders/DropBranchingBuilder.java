package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.SquleException;

public class DropBranchingBuilder extends AbstractSquleBuilder {
	
	public DropBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	public DropTableBuilder table(String table) {
		return new DropTableBuilder(getDialect(), table);
	}

	@Override
	public String generate() {
		throw new SquleException("Invalid method invocation of generate() for Drop Branching Builder");
	}

}
