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
import org.eclipse.dirigible.database.sql.builders.DropBranchingBuilder;
import org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder;

public class DerbyDropBranchingBuilder extends DropBranchingBuilder {
	
	public DerbyDropBranchingBuilder(ISqlDialect dialect) {
		super(dialect);
	}
	
	public DropSequenceBuilder sequence(String sequence) {
		return new DerbyDropSequenceBuilder(this.getDialect(), sequence);
	}
	
}
