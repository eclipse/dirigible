package org.eclipse.dirigible.database.squle.dialects.postgres;

import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class PostgresSquleDialect extends DefaultSquleDialect {

	public NextValueSequenceBuilder nextval(String sequence) {
		return new PostgresNextValueSequenceBuilder(this, sequence);
	}

}
