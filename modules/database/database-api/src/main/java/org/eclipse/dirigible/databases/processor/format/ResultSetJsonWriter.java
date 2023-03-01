/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.databases.processor.format;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.lang3.ClassUtils;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The ResultSet JSON Writer.
 */
public class ResultSetJsonWriter extends AbstractResultSetWriter<String> {

	/** The limited. */
	private boolean limited = true;
	
	/** The stringify. */
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
	 * Sets the stringify.
	 *
	 * @param stringify
	 *            the new stringify
	 */
	public void setStringified(boolean stringify) {
		this.stringify = stringify;
	}

	/**
	 * Write.
	 *
	 * @param resultSet the result set
	 * @return the string
	 * @throws SQLException the SQL exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.databases.processor.format.ResultSetWriter#write(java.sql.ResultSet)
	 */
	@Override
	public String write(ResultSet resultSet) throws SQLException {

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		JsonArray records = new JsonArray();
		int count = 0;
		while (resultSet.next()) {
			JsonObject record = new JsonObject();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String name = resultSetMetaData.getColumnName(i);
				Object value = resultSet.getObject(i);
				if (value == null
						&& stringify) {
					value = "[NULL]";
				}
				if (value != null && !ClassUtils.isPrimitiveOrWrapper(value.getClass())
						&& value.getClass() != String.class
						&& !java.util.Date.class.isAssignableFrom(value.getClass())) {
					if (stringify) {
						value = "[BINARY]";
					}
				}
				record.add(name, GsonHelper.toJsonTree(value));
			}

			records.add(record);

			if (this.isLimited() && (++count > getLimit())) {
				break;
			}
		}

		return GsonHelper.toJson(records);
	}

}
