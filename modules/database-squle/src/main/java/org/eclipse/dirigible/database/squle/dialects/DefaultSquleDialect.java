package org.eclipse.dirigible.database.squle.dialects;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.database.squle.DataType;
import org.eclipse.dirigible.database.squle.ISquleDialect;
import org.eclipse.dirigible.database.squle.ISquleKeywords;

public class DefaultSquleDialect implements ISquleDialect {

	@Override
	public String getDataTypeName(DataType dataType) {
		return dataType.toString();
	}

	@Override
	public String getPrimaryKeyArgument() {
		return KEYWORD_PRIMARY + SPACE + KEYWORD_KEY;
	}
	
	@Override
	public String getNotNullArgument() {
		return KEYWORD_NOT + SPACE + KEYWORD_NULL;
	}

	@Override
	public boolean exists(Connection connection, String table) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, table, ISquleKeywords.METADATA_TABLE_TYPES);
		if (resultSet.next()) {
			return true;
		}
		return false;
	}

}
