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

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.records.DeleteBuilder;

/**
 * The Persistence Delete Processor.
 *
 * @param <T>
 *            the generic type
 */
public class PersistenceDeleteProcessor<T> extends AbstractPersistenceProcessor {

	/**
	 * Instantiates a new persistence delete processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	public PersistenceDeleteProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.
	 * Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		throw new PersistenceException("Generate Script method cannot be invoked in Delete Processor");
	}

	/**
	 * Generate script delete.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
	protected String generateScriptDelete(Connection connection, PersistenceTableModel tableModel) {
		DeleteBuilder deleteBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).delete().from(tableModel.getTableName())
				.where(getPrimaryKey(tableModel) + new StringBuilder().append(ISqlKeywords.SPACE).append(ISqlKeywords.EQUALS)
						.append(ISqlKeywords.SPACE).append(ISqlKeywords.QUESTION).toString());
		String sql = deleteBuilder.toString();
		return sql;
	}

	/**
	 * Generate script delete all.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the string
	 */
	protected String generateScriptDeleteAll(Connection connection, PersistenceTableModel tableModel) {
		DeleteBuilder deleteBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).delete().from(tableModel.getTableName());
		String sql = deleteBuilder.toString();
		return sql;
	}

	/**
	 * Delete.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @param id
	 *            the id
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int delete(Connection connection, PersistenceTableModel tableModel, Class<T> clazz, Object id) throws PersistenceException {
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScriptDelete(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			setValue(preparedStatement, 1, id);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

	/**
	 * Delete all.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @param clazz
	 *            the clazz
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int deleteAll(Connection connection, PersistenceTableModel tableModel, Class<T> clazz) throws PersistenceException {
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScriptDeleteAll(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
	}

}
