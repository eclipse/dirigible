package org.eclipse.dirigible.runtime.databases.processor.format;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetWriter<T> {

	T writeTable(ResultSet rs) throws SQLException;
	
}
