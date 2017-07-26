package org.eclipse.dirigible.database.squle.dialects.hana;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;

public class HanaNextValueSequenceBuilder extends NextValueSequenceBuilder {

	private static final String PATTERN_SELECT_NEXT_VAL_SEQUENCE = "SELECT {0}.NEXTVAL FROM DUMMY";

	public HanaNextValueSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect, sequence);
	}

	@Override
	public String generate() {
		String sql = format(PATTERN_SELECT_NEXT_VAL_SEQUENCE, getSequence());
		return sql;
	}
}
