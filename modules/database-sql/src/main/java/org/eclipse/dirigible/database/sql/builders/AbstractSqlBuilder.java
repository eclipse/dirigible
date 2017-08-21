/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.sql.builders;

import org.eclipse.dirigible.database.sql.ISqlBuilder;
import org.eclipse.dirigible.database.sql.ISqlDialect;

public abstract class AbstractSqlBuilder implements ISqlBuilder {

	private ISqlDialect dialect;

	protected AbstractSqlBuilder(ISqlDialect dialect) {
		this.dialect = dialect;
	}

	protected ISqlDialect getDialect() {
		return dialect;
	}

	/**
	 * Usually returns the default generated snippet
	 */
	@Override
	public String toString() {
		return build();
	}

	/**
	 * Returns the default generated snippet
	 */
	@Override
	public String build() {
		return generate();
	}

}
