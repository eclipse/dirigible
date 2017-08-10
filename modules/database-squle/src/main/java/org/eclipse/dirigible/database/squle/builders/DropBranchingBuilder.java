/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.builders;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.SquleException;
import org.eclipse.dirigible.database.squle.builders.sequence.DropSequenceBuilder;
import org.eclipse.dirigible.database.squle.builders.table.DropTableBuilder;
import org.eclipse.dirigible.database.squle.builders.view.DropViewBuilder;

public class DropBranchingBuilder extends AbstractSquleBuilder {

	public DropBranchingBuilder(ISquleDialect dialect) {
		super(dialect);
	}

	public DropTableBuilder table(String table) {
		return new DropTableBuilder(getDialect(), table);
	}

	public DropViewBuilder view(String view) {
		return new DropViewBuilder(getDialect(), view);
	}

	public DropSequenceBuilder sequence(String sequence) {
		return new DropSequenceBuilder(getDialect(), sequence);
	}

	@Override
	public String generate() {
		throw new SquleException("Invalid method invocation of generate() for Drop Branching Builder");
	}

}
