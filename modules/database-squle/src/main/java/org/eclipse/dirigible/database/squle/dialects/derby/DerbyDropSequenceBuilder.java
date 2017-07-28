/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.dialects.derby;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.sequence.DropSequenceBuilder;

public class DerbyDropSequenceBuilder extends DropSequenceBuilder {
	
	private static final String PATTERN_DROP_SEQUENCE = "DROP SEQUENCE {0} RESTRICT";

	public DerbyDropSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect, sequence);
	}
	
	@Override
	public String generate() {
		String sql = format(PATTERN_DROP_SEQUENCE, getSequence());
		return sql;
	}

}
