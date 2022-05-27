/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.databases.processor.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

/**
 * The ResultSet CSV Writer.
 */
public class ResultSetCsvWriter extends AbstractResultSetWriter<String> {

	private boolean limited = true;
	
	private boolean stringify = true;

	/**
	 * Checks if is limited.
	 *
	 * @return true, if is limited
	 */
	public boolean isLimited() {
		return limited;
	}

	/**
	 * Sets the limited.
	 *
	 * @param limited
	 *            the new limited
	 */
	public void setLimited(boolean limited) {
		this.limited = limited;
	}
	
	/**
	 * Checks if is stringified.
	 *
	 * @return true, if is stringified
	 */
	public boolean isStringified() {
		return stringify;
	}

	/**
	 * Sets the stringified.
	 *
	 * @param stringified
	 *            the new stringified
	 */
	public void setStringified(boolean stringify) {
		this.stringify = stringify;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.databases.processor.format.ResultSetWriter#write(java.sql.ResultSet)
	 */
	@Override
	public String write(ResultSet resultSet) throws SQLException {

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		List<String> records = new ArrayList<String>();
		int count = 0;
		if (resultSet.next()) {
			StringBuffer names = new StringBuffer();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				if (i > 1) {
					names.append(",");
				}
				String name = resultSetMetaData.getColumnName(i);
				names.append("\"" + name + "\"");
			}
			records.add(names.toString());
		} else {
			return "";
		}
		
		count = 0;
		do {
			StringBuffer values = new StringBuffer();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				if (i > 1) {
					values.append(",");
				}
				Object value = resultSet.getObject(i);
				if (value == null
						&& stringify) {
					value = "[NULL]";
				}
				if (value != null 
						&& !ClassUtils.isPrimitiveOrWrapper(value.getClass()) 
						&& value.getClass() != String.class
						&& !java.util.Date.class.isAssignableFrom(value.getClass())) {
					if (stringify) {
						value = "[BINARY]";
					}
				}
				values.append("\"" + value + "\"");
			}

			records.add(values.toString());

			if (this.isLimited() && (++count > getLimit())) {
				break;
			}
		} while (resultSet.next());

		return String.join("\n", records);
	}

}
