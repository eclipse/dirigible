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
package org.eclipse.dirigible.mongodb.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;
import java.util.SortedMap;

public class MongoDBParameterMetaData implements ParameterMetaData {

	public MongoDBParameterMetaData(SortedMap<Integer, Object> parameters) {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if (isWrapperFor(iface)) {
	        return (T) this;
	    }
	    throw new SQLException("No wrapper for " + iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface != null && iface.isAssignableFrom(getClass());
	}

	@Override
	public int getParameterCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int isNullable(int param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isSigned(int param) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPrecision(int param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScale(int param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getParameterType(int param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getParameterTypeName(int param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParameterClassName(int param) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getParameterMode(int param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

}
