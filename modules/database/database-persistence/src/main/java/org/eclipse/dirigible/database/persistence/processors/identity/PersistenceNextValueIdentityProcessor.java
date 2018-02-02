/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence.processors.identity;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.dirigible.database.persistence.IEntityManagerInterceptor;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.parser.Serializer;
import org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Persistence Next Value Identity Processor.
 */
public class PersistenceNextValueIdentityProcessor extends AbstractPersistenceProcessor {

	private static final Logger logger = LoggerFactory.getLogger(PersistenceNextValueIdentityProcessor.class);

	/**
	 * Instantiates a new persistence next value identity processor.
	 *
	 * @param entityManagerInterceptor
	 *            the entity manager interceptor
	 */
	public PersistenceNextValueIdentityProcessor(IEntityManagerInterceptor entityManagerInterceptor) {
		super(entityManagerInterceptor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.database.persistence.processors.AbstractPersistenceProcessor#generateScript(java.sql.
	 * Connection, org.eclipse.dirigible.database.persistence.model.PersistenceTableModel)
	 */
	@Override
	protected String generateScript(Connection connection, PersistenceTableModel tableModel) {
		return null;
	}

	/**
	 * Nextval.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @return the long
	 * @throws PersistenceException
	 *             the persistence exception
	 */
	public long nextval(Connection connection, PersistenceTableModel tableModel) throws PersistenceException {
		logger.trace("nextval -> connection: " + connection.hashCode() + ", tableModel: " + Serializer.serializeTableModel(tableModel));
		PersistenceManager<Identity> persistenceManager = new PersistenceManager<Identity>();
		if (!persistenceManager.tableExists(connection, Identity.class)) {
			persistenceManager.tableCreate(connection, Identity.class);
		}

		Identity identity = persistenceManager.find(connection, Identity.class, tableModel.getTableName());
		if (identity == null) {
			identity = new Identity();
			identity.setTable(tableModel.getTableName());
			identity.setValue(1);
			persistenceManager.insert(connection, identity);
			return 1;
		}

		try {
			boolean autoCommit = connection.getAutoCommit();
			try {
				try {
					if (autoCommit) {
						connection.setAutoCommit(false);
					}
					identity = persistenceManager.lock(connection, Identity.class, tableModel.getTableName());
					identity.setValue(identity.getValue() + 1);
					persistenceManager.update(connection, identity, tableModel.getTableName());
				} finally {
					connection.commit();
				}
			} finally {
				connection.setAutoCommit(autoCommit);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
		return identity.getValue();
	}

}
