package org.eclipse.dirigible.database.squle.dialects.derby;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.sequence.DropSequenceBuilder;

public class DerbyDropSequenceBuilder extends DropSequenceBuilder {
	
	private static final String PATTERN_DROP_SEQUENCE = "DROP SEQUENCE {0} RESTRICT";

	public DerbyDropSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect, sequence);
	}
	
	@Override
	public String generate() {
		String sql = format(PATTERN_DROP_SEQUENCE, getSequence());
		return sql;
	}

}
