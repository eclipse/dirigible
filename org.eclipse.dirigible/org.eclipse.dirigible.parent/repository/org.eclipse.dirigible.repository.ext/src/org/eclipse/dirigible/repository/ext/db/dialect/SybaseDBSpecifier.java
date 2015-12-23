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

public class SybaseDBSpecifier extends RDBGenericDialectSpecifier {

	private static final String SYBASE_TIMESTAMP = "DATETIME"; //$NON-NLS-1$
	private static final String SYBASE_FLOAT = "REAL"; //$NON-NLS-1$
	private static final String SYBASE_BLOB = "IMAGE"; //$NON-NLS-1$
	private static final String SYBASE_CURRENT_TIMESTAMP = "GETDATE()"; //$NON-NLS-1$
	private static final String SYBASE_KEY_VARCHAR = "VARCHAR(1000)";
	private static final String SYBASE_BIG_VARCHAR = "VARCHAR(4000)";

	@Override
	public String specify(String sql) {
		sql = sql.replace(DIALECT_CURRENT_TIMESTAMP, SYBASE_CURRENT_TIMESTAMP);
		sql = sql.replace(DIALECT_TIMESTAMP, SYBASE_TIMESTAMP);
		sql = sql.replace(DIALECT_BLOB, SYBASE_BLOB);
		sql = sql.replace(DIALECT_KEY_VARCHAR, SYBASE_KEY_VARCHAR);
		sql = sql.replace(DIALECT_BIG_VARCHAR, SYBASE_BIG_VARCHAR);
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		if (DBSupportedTypesMap.TIMESTAMP.equals(commonType)) {
			return SYBASE_TIMESTAMP;
		}
		if (DBSupportedTypesMap.FLOAT.equals(commonType)) {
			return SYBASE_FLOAT;
		}
		if (DBSupportedTypesMap.BLOB.equals(commonType)) {
			return SYBASE_BLOB;
		}

		return commonType;
	}

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return ""; //$NON-NLS-1$
	}

	@Override
	public String createTopAndStart(int limit, int offset) {
		return String.format("TOP %d ROWS START AT %d", limit, offset);
	}

	@Override
	public String getAlterAddOpen() {
		return "";
	}

	@Override
	public String getAlterAddClose() {
		return "";
	}

	@Override
	public InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException {
		return new ByteArrayInputStream(resultSet.getBytes(columnName));
	}

	@Override
	public String getAlterAddOpenEach() {
		return "";
	}

	@Override
	public String getAlterAddCloseEach() {
		return "";
	}

}
