/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.builders.sequence;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.AbstractCreateSqlBuilder;

/**
 * The Create Sequence Builder.
 */
public class CreateSequenceBuilder extends AbstractCreateSqlBuilder {

	private String sequence = null;

	private int start = 0;

	private int increment = 1;

	/**
	 * Instantiates a new creates the sequence builder.
	 *
	 * @param dialect
	 *            the dialect
	 * @param sequence
	 *            the sequence
	 */
	public CreateSequenceBuilder(ISqlDialect dialect, String sequence) {
		super(dialect);
		this.sequence = sequence;
	}

	/**
	 * Start.
	 *
	 * @param start
	 *            the start
	 * @return the creates the sequence builder
	 */
	public CreateSequenceBuilder start(int start) {
		this.start = start;
		return this;
	}

	/**
	 * Increment.
	 *
	 * @param increment
	 *            the increment
	 * @return the creates the sequence builder
	 */
	public CreateSequenceBuilder increment(int increment) {
		this.increment = increment;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.ISqlBuilder#generate()
	 */
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

	/**
	 * Generate sequence.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateSequence(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_SEQUENCE).append(SPACE).append(this.sequence);
	}

	/**
	 * Generate start.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateStart(StringBuilder sql) {
		// sql.append(SPACE)
		// .append(KEYWORD_START)
		// .append(SPACE)
		// .append(this.start);
	}

	/**
	 * Generate increment.
	 *
	 * @param sql
	 *            the sql
	 */
	protected void generateIncrement(StringBuilder sql) {
		// sql.append(SPACE)
		// .append(KEYWORD_INCREMENT)
		// .append(SPACE)
		// .append(this.increment);
	}

	/**
	 * Gets the sequence.
	 *
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Gets the increment.
	 *
	 * @return the increment
	 */
	public int getIncrement() {
		return increment;
	}

}
