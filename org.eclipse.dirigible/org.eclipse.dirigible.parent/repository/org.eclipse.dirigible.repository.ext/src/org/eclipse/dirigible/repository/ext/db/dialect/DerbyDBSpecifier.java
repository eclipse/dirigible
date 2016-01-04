/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.ext.db.dialect;

import org.eclipse.dirigible.repository.ext.db.DBSupportedTypesMap;

public class DerbyDBSpecifier extends RDBGenericDialectSpecifier {

	private static final String OFFSET_D_ROWS_FETCH_FIRST_D_ROWS_ONLY = "OFFSET %d ROWS FETCH FIRST %d ROWS ONLY"; //$NON-NLS-1$

	private static final String DERBY_TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	private static final String DERBY_FLOAT = "DOUBLE"; //$NON-NLS-1$
	private static final String DERBY_BLOB = "BLOB"; //$NON-NLS-1$
	private static final String DERBY_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$
	private static final String DERBY_KEY_VARCHAR = "VARCHAR(1000)";
	private static final String DERBY_BIG_VARCHAR = "VARCHAR(4000)";

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return String.format(OFFSET_D_ROWS_FETCH_FIRST_D_ROWS_ONLY, offset, limit);
	}

	@Override
	public String specify(String sql) {
		sql = sql.replace(DIALECT_CURRENT_TIMESTAMP, DERBY_CURRENT_TIMESTAMP);
		sql = sql.replace(DIALECT_TIMESTAMP, DERBY_TIMESTAMP);
		sql = sql.replace(DIALECT_BLOB, DERBY_BLOB);
		sql = sql.replace(DIALECT_KEY_VARCHAR, DERBY_KEY_VARCHAR);
		sql = sql.replace(DIALECT_BIG_VARCHAR, DERBY_BIG_VARCHAR);
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		if (DBSupportedTypesMap.FLOAT.equals(commonType)) {
			return DERBY_FLOAT;
		}
		return commonType;
	}

	@Override
	public String getAlterAddOpen() {
		return " ADD ";
	}

}
