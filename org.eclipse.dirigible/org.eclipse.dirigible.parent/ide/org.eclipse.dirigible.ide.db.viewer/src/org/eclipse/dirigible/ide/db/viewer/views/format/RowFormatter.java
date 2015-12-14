package org.eclipse.dirigible.ide.db.viewer.views.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public interface RowFormatter<T> {
	T write(List<ColumnDescriptor> columnDescriptors, ResultSetMetaData resultSetMetaData, ResultSet resultSet) throws SQLException;
}