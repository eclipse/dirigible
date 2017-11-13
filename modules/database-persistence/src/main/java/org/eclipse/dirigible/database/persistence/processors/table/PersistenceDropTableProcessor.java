/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.table;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.table.DropTableBuilder;

/**
 * The Persistence Drop Table Processor.
 */
public class PersistenceDropTableProcessor extends AbstractPersistenceProcessor {

	/**
	 * Instantiates a new persistence drop table processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	public PersistenceDropTableProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.
	 * Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		DropTableBuilder dropTableBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).drop().table(tableModel.getTableName());

		String sql = dropTableBuilder.toString();
		return sql;
	}

	/**
	 * Drop the table.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the int
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public int drop(Connection connection, PersistenceTableModel tableModel) throws PersistenceException {
		int result = 0;
		String sql = null;
		PreparedStatement preparedStatement = null;
		try {
			sql = generateScript(connection, tableModel);
			preparedStatement = openPreparedStatement(connection, sql);
			result = preparedStatement.executeUpdate();
		} catch (Exception e) {
			throw new PersistenceException(sql, e);
		} finally {
			closePreparedStatement(preparedStatement);
		}
		return result;
	}

}
