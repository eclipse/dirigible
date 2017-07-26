package org.eclipse.dirigible.database.squle.dialects.postgres;

import org.eclipse.dirigible.database.squle.ISquleKeywords;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class PostgresSquleDialect extends DefaultSquleDialect {
	
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIMESTAMP = "current_timestamp"; //$NON-NLS-1$

	public NextValueSequenceBuilder nextval(String sequence) {
		return new PostgresNextValueSequenceBuilder(this, sequence);
	}
	
	@Override
	public String functionCurrentDate() {
		return FUNCTION_CURRENT_DATE;
	}

	@Override
	public String functionCurrentTime() {
		return FUNCTION_CURRENT_TIME;
	}

	@Override
	public String functionCurrentTimestamp() {
		return FUNCTION_CURRENT_TIMESTAMP;
	}

}
