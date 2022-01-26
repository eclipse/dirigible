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

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * The ResultSet Json Writer.
 */
public class ResultSetJsonWriter implements ResultSetWriter<String> {

	private static final int LIMIT = 100;

	private boolean limited = true;

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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.databases.processor.format.ResultSetWriter#write(java.sql.ResultSet)
	 */
	@Override
	public String write(ResultSet resultSet) throws SQLException {

//		StringBuilder tableSb = new StringBuilder();

		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

		JsonArray records = new JsonArray();
		int count = 0;
		while (resultSet.next()) {
			JsonObject record = new JsonObject();
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
				String name = resultSetMetaData.getColumnName(i);
				Object value = resultSet.getObject(i);
				record.add(name, GsonHelper.GSON.toJsonTree(value));
			}

			records.add(record);

			if (this.isLimited() && (++count > LIMIT)) {
//				tableSb.append("..."); //$NON-NLS-1$
				break;
			}
		}

		return GsonHelper.GSON.toJson(records);
	}

}
