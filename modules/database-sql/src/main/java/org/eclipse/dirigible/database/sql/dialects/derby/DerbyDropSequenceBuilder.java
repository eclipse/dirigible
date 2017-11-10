/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.sql.dialects.derby;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class DerbyDropSequenceBuilder.
 */
public class DerbyDropSequenceBuilder extends DropSequenceBuilder {
	
	/** The Constant PATTERN_DROP_SEQUENCE. */
	private static final String PATTERN_DROP_SEQUENCE = "DROP SEQUENCE {0} RESTRICT";

	/**
	 * Instantiates a new derby drop sequence builder.
	 *
	 * @param dialect the dialect
	 * @param sequence the sequence
	 */
	public DerbyDropSequenceBuilder(ISqlDialect dialect, String sequence) {
		super(dialect, sequence);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder#generate()
	 */
	@Override
	public String generate() {
		String sql = format(PATTERN_DROP_SEQUENCE, getSequence());
		return sql;
	}

}
