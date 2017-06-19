package org.eclipse.dirigible.database.squle.dialects.derby;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.DropSequenceBuilder;

public class DerbyDropBranchingBuilder extends DropBranchingBuilder {
	
	public DerbyDropBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	public DropSequenceBuilder sequence(String sequence) {
		return new DerbyDropSequenceBuilder(this.getDialect(), sequence);
	}
	
}
