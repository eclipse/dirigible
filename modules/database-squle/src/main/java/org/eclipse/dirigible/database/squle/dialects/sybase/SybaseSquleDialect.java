package org.eclipse.dirigible.database.squle.dialects.sybase;

import org.eclipse.dirigible.database.squle.builders.records.SelectBuilder;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class SybaseSquleDialect extends DefaultSquleDialect {
	
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIMESTAMP = "getdate()"; //$NON-NLS-1$

	public NextValueSequenceBuilder nextval(String sequence) {
		return new SybaseNextValueSequenceBuilder(this, sequence);
	}
	
	@Override
	public SelectBuilder select() {
		return new SybaseSelectBuilder(this);
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
