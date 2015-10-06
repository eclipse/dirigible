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

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.repository.ext.db.DBSupportedTypesMap;

public class SAPDBSpecifier implements IDialectSpecifier {

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
	public String createTopAndStart(int limit, int offset) {
		return ""; //$NON-NLS-1$
	}

	@Override
	public boolean isSchemaFilterSupported() {
		return false;
	}

	@Override
	public String getSchemaFilterScript() {
		return null;
	}

	@Override
	public String getAlterAddOpen() {
		return " ADD ";
	}

	@Override
	public String getAlterAddClose() {
		return "";
	}

	@Override
	public InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException {
		Blob data = resultSet.getBlob(columnName);
		return data.getBinaryStream();
	}

	@Override
	public boolean isCatalogForSchema() {
		return false;
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
