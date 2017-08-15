package org.eclipse.dirigible.databases.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.databases.processor.format.ResultSetStringWriter;

public class DatabaseResultSetHelper {

	public static String printResultSet(ResultSet resultSet, boolean limited) throws SQLException {
		ResultSetStringWriter writer = new ResultSetStringWriter();
		writer.setLimited(limited);
		String tableString = writer.writeTable(resultSet);
		return tableString;
	}

}
