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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.repository.ext.db.DBSupportedTypesMap;

public class PostgreSQLDBSpecifier extends RDBGenericDialectSpecifier {

	private static final String LIMIT_D_OFFSET_D = "LIMIT %d OFFSET %d"; //$NON-NLS-1$

	private static final String POSTGRESQL_TIMESTAMP = "TIMESTAMP"; //$NON-NLS-1$
	private static final String POSTGRESQL_FLOAT = "REAL"; //$NON-NLS-1$
	private static final String POSTGRESQL_DOUBLE = "DOUBLE PRECISION"; //$NON-NLS-1$
	private static final String POSTGRESQL_BLOB = "BYTEA"; //$NON-NLS-1$
	private static final String POSTGRESQL_CLOB = "TEXT"; //$NON-NLS-1$
	private static final String POSTGRESQL_CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP"; //$NON-NLS-1$
	private static final String POSTGRESQL_KEY_VARCHAR = "VARCHAR(1000)";
	private static final String POSTGRESQL_BIG_VARCHAR = "VARCHAR(4000)";

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return String.format(LIMIT_D_OFFSET_D, limit, offset);
	}

	@Override
	public String specify(String sql) {
		sql = sql.replace(DIALECT_CURRENT_TIMESTAMP, POSTGRESQL_CURRENT_TIMESTAMP);
		sql = sql.replace(DIALECT_TIMESTAMP, POSTGRESQL_TIMESTAMP);
		sql = sql.replace(DIALECT_BLOB, POSTGRESQL_BLOB);
		sql = sql.replace(DIALECT_KEY_VARCHAR, POSTGRESQL_KEY_VARCHAR);
		sql = sql.replace(DIALECT_BIG_VARCHAR, POSTGRESQL_BIG_VARCHAR);
		sql = sql.replace(DIALECT_CLOB, POSTGRESQL_CLOB);
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		if (DBSupportedTypesMap.FLOAT.equals(commonType)) {
			return POSTGRESQL_FLOAT;
		}
		if (DBSupportedTypesMap.DOUBLE.equals(commonType)) {
			return POSTGRESQL_DOUBLE;
		}
		if (DBSupportedTypesMap.BLOB.equals(commonType)) {
			return POSTGRESQL_BLOB;
		}
		if (DBSupportedTypesMap.CLOB.equals(commonType)) {
			return POSTGRESQL_CLOB;
		}
		return commonType;
	}

	@Override
	public InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException {
		return new ByteArrayInputStream(resultSet.getBytes(columnName));
	}

	@Override
	public String getAlterAddOpenEach() {
		return " ADD COLUMN ";
	}
}
