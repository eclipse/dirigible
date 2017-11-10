/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.eclipse.dirigible.database.sql.ISqlKeywords;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.database.sql.builders.sequence.DropSequenceBuilder;

// TODO: Auto-generated Javadoc
/**
 * The Class PersistenceDropSequenceProcessor.
 */
public class PersistenceDropSequenceProcessor extends AbstractPersistenceProcessor {

	/**
	 * Instantiates a new persistence drop sequence processor.
	 *
	 * @param entityManagerInterceptor the entity manager interceptor
	 */
	public PersistenceDropSequenceProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		DropSequenceBuilder dropSequenceBuilder = SqlFactory.getNative(SqlFactory.deriveDialect(connection)).drop()
				.sequence(tableModel.getTableName() + ISqlKeywords.UNDERSCROE + ISqlKeywords.KEYWORD_SEQUENCE);

		String sql = dropSequenceBuilder.toString();
		return sql;
	}

	/**
	 * Drop.
	 *
	 * @param connection the connection
	 * @param tableModel the table model
	 * @return the int
	 * @throws PersistenceException the persistence exception
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
