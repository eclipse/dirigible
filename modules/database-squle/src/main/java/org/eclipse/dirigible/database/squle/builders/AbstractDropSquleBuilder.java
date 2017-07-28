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

public abstract class AbstractDropSquleBuilder extends AbstractSquleBuilder {

	protected AbstractDropSquleBuilder(ISquleDialect dialect) {
		super(dialect);
	}
	
	protected void generateDrop(StringBuilder sql) {
		sql.append(KEYWORD_DROP);
	}

}
