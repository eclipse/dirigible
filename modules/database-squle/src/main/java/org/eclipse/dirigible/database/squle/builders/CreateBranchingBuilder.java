package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.SquleException;

public class CreateBranchingBuilder extends AbstractSquleBuilder {
	
	public CreateBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	public CreateTableBuilder table(String table) {
		return new CreateTableBuilder(getDialect(), table);
	}

	@Override
	public String generate() {
		throw new SquleException("Invalid method invocation of generate() for Create Branching Builder");
	}

}
