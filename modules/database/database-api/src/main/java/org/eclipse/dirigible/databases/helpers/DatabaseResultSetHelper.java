/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.databases.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.dirigible.databases.processor.format.ResultSetJsonWriter;
import org.eclipse.dirigible.databases.processor.format.ResultSetMonospacedWriter;

/**
 * The Database Result SetHelper.
 */
public class DatabaseResultSetHelper {

	/**
	 * Prints the provided ResultSet to the {@link ResultSetMonospacedWriter} writer.
	 *
	 * @param resultSet
	 *            the result set
	 * @param limited
	 *            the limited
	 * @return the string
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String print(ResultSet resultSet, boolean limited) throws SQLException {
		ResultSetMonospacedWriter writer = new ResultSetMonospacedWriter();
		writer.setLimited(limited);
		String result = writer.write(resultSet);
		return result;
	}

	/**
	 * Prints the provided ResultSet to the {@link ResultSetJsonWriter} writer.
	 *
	 * @param resultSet
	 *            the result set
	 * @param limited
	 *            the limited
	 * @return the string
	 * @throws SQLException
	 *             the SQL exception
	 */
	public static String toJson(ResultSet resultSet, boolean limited) throws SQLException {
		ResultSetJsonWriter writer = new ResultSetJsonWriter();
		writer.setLimited(limited);
		String result = writer.write(resultSet);
		return result;
	}

}
