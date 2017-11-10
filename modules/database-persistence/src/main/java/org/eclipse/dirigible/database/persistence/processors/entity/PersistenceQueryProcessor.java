/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;

public class PersistenceQueryProcessor<T> extends AbstractPersistenceProcessor {

	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		throw new PersistenceException("Generate Script method cannot be invoked in Query Processor");
	}

	protected String generateScriptFind(Connection connection, PersistenceTableModel tableModel) {
		SelectBuilder selectBuilder = SqlFactory.getNative(connection).select().column("*").from(tableModel.getTableName());
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (columnModel.isPrimaryKey()) {
				selectBuilder.where(new StringBuilder().append(columnModel.getName()).append(ISqlKeywords.SPACE).append(ISqlKeywords.EQUALS)
						.append(ISqlKeywords.SPACE).append(ISqlKeywords.QUESTION).toString());
				break;
			}
		}
		String sql = selectBuilder.toString();
		return sql;
	}

	protected String generateScriptLock(Connection connection, PersistenceTableModel tableModel) {
		SelectBuilder selectBuilder = SqlFactory.getNative(connection).select().column("*").from(tableModel.getTableName());
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (columnModel.isPrimaryKey()) {
				selectBuilder.where(new StringBuilder().append(columnModel.getName()).append(ISqlKeywords.SPACE).append(ISqlKeywords.EQUALS)
						.append(ISqlKeywords.SPACE).append(ISqlKeywords.QUESTION).toString());
				break;
			}
		}
		selectBuilder.forUpdate();
		String sql = selectBuilder.build();
		return sql;
	}

	protected String generateScriptFindAll(Connection connection, PersistenceTableModel tableModel) {
		SelectBuilder selectBuilder = SqlFactory.getNative(connection).select().column("*").from(tableModel.getTableName());
		String sql = selectBuilder.toString();
		return sql;
	}

	public T find(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = generateScriptFind(connection, tableModel);
		return get(connection, tableModel, clazz, id, sql);
	}

	public T lock(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = generateScriptLock(connection, tableModel);
		return get(connection, tableModel, clazz, id, sql);
	}

	protected T get(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id, String sql) throws PersistenceException {
		T result = null;
		PreparedStatement preparedStatement = null;
		try {
			result = clazz.newInstance();
			preparedStatement = openPreparedStatement(connection, sql);
			setValuePrimaryKey(tableModel, id, preparedStatement);
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
						setValueToPojo(result, resultSet, columnModel);
					}
				} else {
					return null;
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

	public List<T> findAll(Connection connection, PersistenceTableModel tableModel, Class<T> clazz) throws PersistenceException {
		List<T> result = new ArrayList<T>();
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScriptFindAll(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					T pojo = clazz.newInstance();
					for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
						setValueToPojo(pojo, resultSet, columnModel);
					}
					result.add(pojo);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

	public List<T> query(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, String sql, List<Object> values) {
		List<T> result = new ArrayList<T>();
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = openPreparedStatement(connection, sql);
			if (values != null) {
				int i = 1;
				for (Object value : values) {
					setValue(preparedStatement, i++, value);
				}
			}
			ResultSet resultSet = null;
			try {
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					T pojo = clazz.newInstance();
					for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
						setValueToPojo(pojo, resultSet, columnModel);
					}
					result.add(pojo);
				}
			} finally {
				if (resultSet != null) {
					resultSet.close();
				}
			}
		} catch (Exception e) {
			throw new PersistenceException(e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

}
