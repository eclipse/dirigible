/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.database.squle.builders.view;

import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.builders.AbstractDropSquleBuilder;

public class DropViewBuilder extends AbstractDropSquleBuilder {

	private String view = null;

	public DropViewBuilder(ISquleDialect dialect, String view) {
		super(dialect);
		this.view = view;
	}

	@Override
	public String generate() {

		StringBuilder sql = new StringBuilder();

		// DROP
		generateDrop(sql);

		// VIEW
		generateView(sql);

		return sql.toString();
	}

	protected void generateView(StringBuilder sql) {
		sql.append(SPACE).append(KEYWORD_VIEW).append(SPACE).append(this.view);
	}

}
