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

import org.eclipse.dirigible.database.sql.ISqlDialect;
import org.eclipse.dirigible.database.sql.builders.sequence.CreateSequenceBuilder;

public class DerbyCreateSequenceBuilder extends CreateSequenceBuilder {

	public DerbyCreateSequenceBuilder(ISqlDialect dialect, String sequence) {
		super(dialect, sequence);
	}

	@Override
	protected void generateStart(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_AS).append(SPACE).append("BIGINT").append(SPACE).append(KEYWORD_START).append(SPACE).append(KEYWORD_WITH)
				.append(SPACE).append(0);
	}

}
