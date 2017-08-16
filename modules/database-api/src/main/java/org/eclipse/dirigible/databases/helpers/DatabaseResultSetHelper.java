package org.eclipse.dirigible.databases.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.databases.processor.format.ResultSetJsonWriter;
import org.eclipse.dirigible.databases.processor.format.ResultSetMonospacedWriter;

public class DatabaseResultSetHelper {

	public static String print(ResultSet resultSet, boolean limited) throws SQLException {
		ResultSetMonospacedWriter writer = new ResultSetMonospacedWriter();
		writer.setLimited(limited);
		String result = writer.write(resultSet);
		return result;
	}

	public static String toJson(ResultSet resultSet, boolean limited) throws SQLException {
		ResultSetJsonWriter writer = new ResultSetJsonWriter();
		writer.setLimited(limited);
		String result = writer.write(resultSet);
		return result;
	}

}
