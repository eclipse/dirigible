/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.repository.RepositoryFacade;
import org.eclipse.dirigible.runtime.scripting.AbstractScriptExecutor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class SQLExecutor extends AbstractScriptExecutor {

	private static final String SQL_MODULE_NAME_CANNOT_BE_NULL = "SQL module name cannot be null.";

	private static final Logger logger = Logger.getLogger(SQLExecutor.class);

	private IRepository repository;
	private String[] rootPaths;
	private Map<String, Object> defaultVariables;

	private String classpath;

	public SQLExecutor(IRepository repository, String... rootPaths) {
		this.repository = repository;
		this.rootPaths = rootPaths;
		this.defaultVariables = new HashMap<String, Object>();
		this.classpath = classpath;
	}

	@Override
	public Object executeServiceModule(HttpServletRequest request, HttpServletResponse response, Object input, String module,
			Map<Object, Object> executionContext) throws IOException {

		String result = null;
		try {
			logger.debug("entering: executeServiceModule()"); //$NON-NLS-1$
			logger.debug("module=" + module); //$NON-NLS-1$

			if (module == null) {
				throw new IOException(SQL_MODULE_NAME_CANNOT_BE_NULL);
			}

			String sqlSource = new String(retrieveModule(repository, module, "", rootPaths).getContent());

			DataSource dataSource = RepositoryFacade.getInstance().getDataSource();
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				PreparedStatement pstmt = connection.prepareStatement(sqlSource);
				ResultSet rs = pstmt.executeQuery();

				// get column names
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCnt = rsmd.getColumnCount();
				List<String> columnNames = new ArrayList<String>();
				for (int i = 1; i <= columnCnt; i++) {
					columnNames.add(rsmd.getColumnName(i).toUpperCase());
				}

				JsonArray array = new JsonArray();
				while (rs.next()) {
					JsonObject obj = new JsonObject();
					for (int i = 1; i <= columnCnt; i++) {
						String key = columnNames.get(i - 1);
						String value = rs.getString(i);
						obj.add(key, new JsonPrimitive(value != null ? value : ""));
					}
					array.add(obj);
				}

				result = new Gson().toJson(array);

				rs.close();
				pstmt.close();
			} finally {
				if (connection != null) {
					connection.close();
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IOException(e);
		}

		return result;
	}

	@Override
	protected void registerDefaultVariable(Object scope, String name, Object value) {
		defaultVariables.put(name, value);
	}

	@Override
	protected String getModuleType(String path) {
		return ICommonConstants.ARTIFACT_TYPE.SCRIPTING_SERVICES;
	}
}
