package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.SquleException;
import org.eclipse.dirigible.database.squle.builders.sequence.DropSequenceBuilder;
import org.eclipse.dirigible.database.squle.builders.table.DropTableBuilder;

public class DropBranchingBuilder extends AbstractSquleBuilder {
	
	public DropBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	public DropTableBuilder table(String table) {
		return new DropTableBuilder(getDialect(), table);
	}
	
	public DropSequenceBuilder sequence(String sequence) {
		return new DropSequenceBuilder(getDialect(), sequence);
	}

	@Override
	public String generate() {
		throw new SquleException("Invalid method invocation of generate() for Drop Branching Builder");
	}

}
