/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.dialects.hana;

import static java.text.MessageFormat.format;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.sequence.NextValueSequenceBuilder;

public class HanaNextValueSequenceBuilder extends NextValueSequenceBuilder {

	private static final String PATTERN_SELECT_NEXT_VAL_SEQUENCE = "SELECT {0}.NEXTVAL FROM DUMMY";

	public HanaNextValueSequenceBuilder(ISquleDialect dialect, String sequence) {
		super(dialect, sequence);
	}

	@Override
	public String generate() {
		String sql = format(PATTERN_SELECT_NEXT_VAL_SEQUENCE, getSequence());
		return sql;
	}
}
