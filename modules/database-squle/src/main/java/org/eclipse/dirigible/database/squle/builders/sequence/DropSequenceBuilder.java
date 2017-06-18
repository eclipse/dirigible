package org.eclipse.dirigible.database.squle.builders.sequence;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractDropSquleBuilder;

public class DropSequenceBuilder extends AbstractDropSquleBuilder {
	
	private String sequence = null;
	
	public DropSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect);
		this.sequence = sequence;
	}

	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// DROP
		generateDrop(sql);
		
		// SEQUENCE
		generateSequence(sql);
		
		return sql.toString();
	}
	
	protected void generateSequence(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_SEQUENCE)
			.append(SPACE)
			.append(this.sequence);
	}
	
	public String getSequence() {
		return sequence;
	}
	
}
