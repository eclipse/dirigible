package org.eclipse.dirigible.database.squle.dialects.sybase;

import org.eclipse.dirigible.database.squle.builders.CreateBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.squle.builders.records.DeleteBuilder;
import org.eclipse.dirigible.database.squle.builders.records.InsertBuilder;
import org.eclipse.dirigible.database.squle.builders.records.UpdateBuilder;
import org.eclipse.dirigible.database.squle.dialects.DefaultSquleDialect;

public class SybaseSquleDialect extends DefaultSquleDialect<SybaseSelectBuilder, InsertBuilder, UpdateBuilder, DeleteBuilder, CreateBranchingBuilder, DropBranchingBuilder, SybaseNextValueSequenceBuilder> {
	
	public static final String FUNCTION_CURRENT_DATE = "current_date"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIME = "current_time"; //$NON-NLS-1$
	public static final String FUNCTION_CURRENT_TIMESTAMP = "getdate()"; //$NON-NLS-1$

	public SybaseNextValueSequenceBuilder nextval(String sequence) {
		return new SybaseNextValueSequenceBuilder(this, sequence);
	}
	
	@Override
	public SybaseSelectBuilder select() {
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
