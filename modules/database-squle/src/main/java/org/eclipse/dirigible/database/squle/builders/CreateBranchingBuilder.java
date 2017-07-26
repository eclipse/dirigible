package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.SquleException;
import org.eclipse.dirigible.database.squle.builders.sequence.CreateSequenceBuilder;
import org.eclipse.dirigible.database.squle.builders.table.CreateTableBuilder;

public class CreateBranchingBuilder extends AbstractSquleBuilder {
	
	public CreateBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	public CreateTableBuilder table(String table, Object...args) {
		return new CreateTableBuilder(getDialect(), table);
	}
	
	public CreateSequenceBuilder sequence(String sequence) {
		return new CreateSequenceBuilder(getDialect(), sequence);
	}

	@Override
	public String generate() {
		throw new SquleException("Invalid method invocation of generate() for Create Branching Builder");
	}

}
