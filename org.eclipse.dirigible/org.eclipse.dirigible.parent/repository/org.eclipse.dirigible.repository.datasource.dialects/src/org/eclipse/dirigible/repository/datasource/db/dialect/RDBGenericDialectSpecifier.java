package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("javadoc")
public class RDBGenericDialectSpecifier implements IDialectSpecifier {

	private static final String DOT = "."; //$NON-NLS-1$
	private static final String QUOTES = "\""; //$NON-NLS-1$
	private static final String EMPTY = "";

	@Override
	public String specify(String sql) {
		return sql;
	}

	@Override
	public String getSpecificType(String commonType) {
		return commonType;
	}

	@Override
	public String createLimitAndOffset(int limit, int offset) {
		return "";
	}

	@Override
	public String createTopAndStart(int limit, int offset) {
		return "";
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
		return "";
	}

	@Override
	public String getAlterAddOpenEach() {
		return "";
	}

	@Override
	public String getAlterAddClose() {
		return "";
	}

	@Override
	public String getAlterAddCloseEach() {
		return "";
	}

	/**
	 * Default implementation using
	 *
	 * <PRE>
	 * resultSet.getBlob(columnName).getBinaryStream()
	 * </PRE>
	 *
	 * to get InputStream bytes.
	 */
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
	public boolean isSchemaless() {
		return false;
	}

	@Override
	public boolean isDialectForName(String productName) {
		return false;
	}

	@Override
	public String getContentQueryScript(String catalogName, String schemaName, String tableName) {
		if (tableName == null) {
			throw new IllegalArgumentException();
		}
		String q = "SELECT * FROM ";
		String tableFqn = (schemaName != null ? QUOTES + schemaName + QUOTES + DOT : EMPTY) + QUOTES + tableName + QUOTES;
		return q + tableFqn;
	}

}
