package org.eclipse.dirigible.database.squle.builders.sequence;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractQuerySquleBuilder;

public class NextValueSequenceBuilder extends AbstractQuerySquleBuilder {
	
	private String sequence = null;
	
	public NextValueSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect);
		this.sequence = sequence;
	}
	
	
	@Override
	public String generate() {
		StringBuilder sql = new StringBuilder();
		
		// SELECT
		generateSelect(sql);
		
		// NEXTVAL
		generateNextValue(sql);
		
		return sql.toString();
	}

	protected void generateSelect(StringBuilder sql) {
		sql.append(KEYWORD_SELECT);
	}

	protected void generateNextValue(StringBuilder sql) {
			sql.append(SPACE)
				.append(KEYWORD_NEXT_VALUE_FOR)
				.append(SPACE)
				.append(sequence);
	}
	
	public String getSequence() {
		return sequence;
	}

}
