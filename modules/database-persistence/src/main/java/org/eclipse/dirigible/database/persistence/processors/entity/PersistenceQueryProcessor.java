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

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.SelectBuilder;

/**
 * The Persistence Query Processor.
 *
 * @param <T>
 *            the generic type
 */
public class PersistenceQueryProcessor<T> extends AbstractPersistenceProcessor {

	/**
	 * Instantiates a new persistence query processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	public PersistenceQueryProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.
	 * Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		throw new PersistenceException("Generate Script method cannot be invoked in Query Processor");
	}

	/**
	 * Generate script find.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
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

	/**
	 * Generate script lock.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
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

	/**
	 * Generate script find all.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
	protected String generateScriptFindAll(Connection connection, PersistenceTableModel tableModel) {
		SelectBuilder selectBuilder = SqlFactory.getNative(connection).select().column("*").from(tableModel.getTableName());
		String sql = selectBuilder.toString();
		return sql;
	}

	/**
	 * Find.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return the t
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public T find(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = generateScriptFind(connection, tableModel);
		return get(connection, tableModel, clazz, id, sql);
	}

	/**
	 * Lock.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return the t
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public T lock(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = generateScriptLock(connection, tableModel);
		return get(connection, tableModel, clazz, id, sql);
	}

	/**
	 * Gets the.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @param sql
	 *            the sql
	 * @return the t
	 * @throws PersistenceException
	 *             the persistence exception
	 */
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

	/**
	 * Find all.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @return the list
	 * @throws PersistenceException
	 *             the persistence exception
	 */
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

	/**
	 * Query.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @param sql
	 *            the sql
	 * @param values
	 *            the values
	 * @return the list
	 */
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
