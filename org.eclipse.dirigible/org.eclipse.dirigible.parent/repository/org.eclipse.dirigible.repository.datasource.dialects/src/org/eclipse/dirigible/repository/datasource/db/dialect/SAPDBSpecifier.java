/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.repository.datasource.db.dialect;

import org.eclipse.dirigible.repository.datasource.DBSupportedTypesMap;

public class SAPDBSpecifier extends RDBGenericDialectSpecifier {

	public static final String PRODUCT_SAP_DB = "SAP DB"; //$NON-NLS-1$

	private static final String LIMIT_D_D = "LIMIT %d, %d";
	private static final String SAPDB_TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	private static final String SAPDB_FLOAT = "DOUBLE"; //$NON-NLS-1$
	private static final String SAPDB_BLOB = "BLOB"; //$NON-NLS-1$
	private static final String SAPDB_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$
	private static final String SAPDB_KEY_VARCHAR = "VARCHAR(1000)";
	private static final String SAPDB_BIG_VARCHAR = "VARCHAR(4000)";

	@Override
	public String specify(String sql) {
		sql = sql.replace(DIALECT_CURRENT_TIMESTAMP, SAPDB_CURRENT_TIMESTAMP);
		sql = sql.replace(DIALECT_TIMESTAMP, SAPDB_TIMESTAMP);
		sql = sql.replace(DIALECT_BLOB, SAPDB_BLOB);
		sql = sql.replace(DIALECT_KEY_VARCHAR, SAPDB_KEY_VARCHAR);
		sql = sql.replace(DIALECT_BIG_VARCHAR, SAPDB_BIG_VARCHAR);
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		if (DBSupportedTypesMap.FLOAT.equals(commonType)) {
			return SAPDB_FLOAT;
		}
		return commonType;
	}

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return String.format(LIMIT_D_D, offset, limit);
	}

	@Override
	public String getAlterAddOpen() {
		return " ADD ";
	}

	@Override
	public boolean isDialectForName(String productName) {
		return PRODUCT_SAP_DB.equalsIgnoreCase(productName);
	}

}
