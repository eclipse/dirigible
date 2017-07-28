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

import org.eclipse.dirigible.database.squle.ISquleBuilder;
import org.eclipse.dirigible.database.squle.ISquleDialect;

public abstract class AbstractSquleBuilder implements ISquleBuilder {

	private ISquleDialect dialect;

	protected AbstractSquleBuilder(ISquleDialect dialect) {
		this.dialect = dialect;
	}

	protected ISquleDialect getDialect() {
		return dialect;
	}
	/**
	 * Usually returns the default generated snippet
	 */
	@Override
	public String toString() {
		return generate();
	}
	
}
