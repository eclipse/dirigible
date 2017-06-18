package org.eclipse.dirigible.database.squle.builders.sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractCreateSquleBuilder;

public class CreateSequenceBuilder extends AbstractCreateSquleBuilder {
	
	private String sequence = null;
	private int start = 0;
	private int increment = 1;
	
	public CreateSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect);
		this.sequence = sequence;
	}
	
	public CreateSequenceBuilder start(int start) {
		this.start = start;
		return this;
	}
	
	public CreateSequenceBuilder increment(int increment) {
		this.increment = increment;
		return this;
	}

	@Override
	public String generate() {
		
		StringBuilder sql = new StringBuilder();
		
		// CREATE
		generateCreate(sql);
		
		// SEQUENCE
		generateSequence(sql);
		
//		// START
//		generateStart(sql);
//		
//		// INCREMENT
//		generateIncrement(sql);
		
		return sql.toString();
	}
	
	protected void generateSequence(StringBuilder sql) {
		sql.append(SPACE)
			.append(KEYWORD_SEQUENCE)
			.append(SPACE)
			.append(this.sequence);
	}
	
//	protected void generateStart(StringBuilder sql) {
//		sql.append(SPACE)
//			.append(KEYWORD_START)
//			.append(SPACE)
//			.append(this.start);
//	}
//	
//	protected void generateIncrement(StringBuilder sql) {
//		sql.append(SPACE)
//			.append(KEYWORD_INCREMENT)
//			.append(SPACE)
//			.append(this.increment);
//	}
	
	public String getSequence() {
		return sequence;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getIncrement() {
		return increment;
	}
	
}
