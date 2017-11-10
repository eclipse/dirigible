/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

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
