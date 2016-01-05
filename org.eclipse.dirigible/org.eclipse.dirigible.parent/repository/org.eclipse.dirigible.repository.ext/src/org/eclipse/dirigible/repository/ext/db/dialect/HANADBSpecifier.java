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

public class HANADBSpecifier extends RDBGenericDialectSpecifier {

	public static final String PRODUCT_HDB = "HDB"; //$NON-NLS-1$

	private static final String LIMIT_D_OFFSET_D = "LIMIT %d OFFSET %d"; //$NON-NLS-1$
	private static final String HANA_FLOAT = "DOUBLE"; //$NON-NLS-1$
	private static final String HANA_TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	private static final String HANA_BLOB = "BLOB"; //$NON-NLS-1$
	private static final String HANA_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$
	private static final String HANA_KEY_VARCHAR = "VARCHAR(1000)";
	private static final String HANA_BIG_VARCHAR = "VARCHAR(4000)";

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return String.format(LIMIT_D_OFFSET_D, limit, offset);
	}

	@Override
	public boolean isSchemaFilterSupported() {
		return true;
	}

	@Override
	public String getSchemaFilterScript() {
		return "SELECT * FROM PUBLIC.SCHEMAS WHERE HAS_PRIVILEGES='TRUE'";
	}

	@Override
	public String getAlterAddOpen() {
		return " ADD ( ";
	}

	@Override
	public String getAlterAddClose() {
		return " ) ";
	}

	@Override
	public String specify(String sql) {
		sql = sql.replace(DIALECT_CURRENT_TIMESTAMP, HANA_CURRENT_TIMESTAMP);
		sql = sql.replace(DIALECT_TIMESTAMP, HANA_TIMESTAMP);
		sql = sql.replace(DIALECT_BLOB, HANA_BLOB);
		sql = sql.replace(DIALECT_KEY_VARCHAR, HANA_KEY_VARCHAR);
		sql = sql.replace(DIALECT_BIG_VARCHAR, HANA_BIG_VARCHAR);
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		if (DBSupportedTypesMap.FLOAT.equals(commonType)) {
			return HANA_FLOAT;
		}
		return commonType;
	}

	@Override
	public boolean isDialectForName(String productName) {
		return PRODUCT_HDB.equalsIgnoreCase(productName);
	}

}
