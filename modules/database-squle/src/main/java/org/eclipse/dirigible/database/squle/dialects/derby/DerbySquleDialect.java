package org.eclipse.dirigible.database.squle.dialects.derby;

import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class DerbySquleDialect extends DefaultSquleDialect {

	public NextValueSequenceBuilder nextval(String sequence) {
		return new DerbyNextValueSequenceBuilder(this, sequence);
	}

	@Override
	public DropBranchingBuilder drop() {
		return new DerbyDropBranchingBuilder(this);
	}
	
}
