package org.eclipse.dirigible.ide.db.viewer.views.format;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetWriter<T> {

	T writeTable(ResultSet rs) throws SQLException;
	
}
