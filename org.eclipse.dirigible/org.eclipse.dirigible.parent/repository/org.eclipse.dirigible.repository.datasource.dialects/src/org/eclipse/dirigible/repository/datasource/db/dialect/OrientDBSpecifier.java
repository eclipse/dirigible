package org.eclipse.dirigible.repository.datasource.db.dialect;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("javadoc")
public class OrientDBSpecifier implements IDialectSpecifier {

	public static final String PRODUCT_MONGODB = "OrientDB"; //$NON-NLS-1$

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
		return null;
	}

	@Override
	public String createTopAndStart(int limit, int offset) {
		return null;
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
		return null;
	}

	@Override
	public String getAlterAddOpenEach() {
		return null;
	}

	@Override
	public String getAlterAddClose() {
		return null;
	}

	@Override
	public String getAlterAddCloseEach() {
		return null;
	}

	@Override
	public InputStream getBinaryStream(ResultSet resultSet, String columnName) throws SQLException {
		return resultSet.getBinaryStream(columnName);
	}

	@Override
	public boolean isCatalogForSchema() {
		return false;
	}

	@Override
	public String getContentQueryScript(String catalogName, String schemaName, String tableName) {
		return "SELECT * " + tableName;
	}

	@Override
	public boolean isSchemaless() {
		return true;
	}

	@Override
	public boolean isDialectForName(String productName) {
		return PRODUCT_MONGODB.equalsIgnoreCase(productName);
	}
}
