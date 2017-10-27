/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.builders.sequence;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;

public class CreateSequenceBuilder extends AbstractCreateSqlBuilder {

	private String sequence = null;
	private int start = 0;
	private int increment = 1;

	public CreateSequenceBuilder(ISqlDialect dialect, String sequence) {
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

		// // START
		generateStart(sql);
		//
		// // INCREMENT
		generateIncrement(sql);

		return sql.toString();
	}

	protected void generateSequence(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_SEQUENCE).append(SPACE).append(this.sequence);
	}

	protected void generateStart(StringBuilder sql) {
		// sql.append(SPACE)
		// .append(KEYWORD_START)
		// .append(SPACE)
		// .append(this.start);
	}

	protected void generateIncrement(StringBuilder sql) {
		// sql.append(SPACE)
		// .append(KEYWORD_INCREMENT)
		// .append(SPACE)
		// .append(this.increment);
	}

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
