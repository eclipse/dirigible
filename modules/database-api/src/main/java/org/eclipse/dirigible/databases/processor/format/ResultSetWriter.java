package org.eclipse.dirigible.databases.processor.format;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetWriter<T> {

	T write(ResultSet rs) throws SQLException;
	
}
