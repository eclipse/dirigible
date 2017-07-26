package org.eclipse.dirigible.database.squle.dialects.sybase;

import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class SybaseSquleDialect extends DefaultSquleDialect {

	public NextValueSequenceBuilder nextval(String sequence) {
		return new SybaseNextValueSequenceBuilder(this, sequence);
	}

}
