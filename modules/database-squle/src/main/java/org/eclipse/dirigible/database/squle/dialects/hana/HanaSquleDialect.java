package org.eclipse.dirigible.database.squle.dialects.hana;

import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class HanaSquleDialect extends DefaultSquleDialect {

	public NextValueSequenceBuilder nextval(String sequence) {
		return new HanaNextValueSequenceBuilder(this, sequence);
	}

	@Override
	public HanaCreateBranchingBuilder create() {
		return new HanaCreateBranchingBuilder(this);
	}
}
