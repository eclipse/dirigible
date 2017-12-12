/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.database.persistence;

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.GenerationType;

import org.eclipse.dirigible.database.persistence.model.PersistenceTableColumnModel;
import org.eclipse.dirigible.database.persistence.model.PersistenceTableModel;
import org.eclipse.dirigible.database.persistence.processors.entity.PersistenceDeleteProcessor;
import org.eclipse.dirigible.database.persistence.processors.entity.PersistenceExecuteProcessor;
import org.eclipse.dirigible.database.persistence.processors.entity.PersistenceInsertProcessor;
import org.eclipse.dirigible.database.persistence.processors.entity.PersistenceQueryProcessor;
import org.eclipse.dirigible.database.persistence.processors.entity.PersistenceUpdateProcessor;
import org.eclipse.dirigible.database.persistence.processors.identity.PersistenceCreateIdentityProcessor;
import org.eclipse.dirigible.database.persistence.processors.sequence.PersistenceCreateSequenceProcessor;
import org.eclipse.dirigible.database.persistence.processors.sequence.PersistenceDropSequenceProcessor;
import org.eclipse.dirigible.database.persistence.processors.table.PersistenceCreateTableProcessor;
import org.eclipse.dirigible.database.persistence.processors.table.PersistenceDropTableProcessor;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PersistenceManager is a simple transport mechanism to store and retrieve
 * POJO object to/from underlying JDBC compliant database.
 * It reads a limited set of the standard JPA annotations from the POJO, such as Table, Id, Column, etc.
 * and generate a dialect dependent SQL script.
 * It works on flat POJOs and no lazy loading, associations, caches, etc. are supported.
 * It is the simplest possible persistence channel for POJOs and will stay at this level in the future
 * The POJO supported by this manager must have a single mandatory Id (PRIMARY KEY) field
 *
 * @param <T>
 *            type safety for a PersistenceManager instance
 */
public class PersistenceManager<T> {

	private static final String CONNECTION_ID_SEPARATOR = ":";

	private static final Logger logger = LoggerFactory.getLogger(PersistenceManager.class);

	/** The Constant EXISTING_TABLES_CACHE. */
	private static final List<String> EXISTING_TABLES_CACHE = Collections.synchronizedList(new ArrayList<String>());

	/** The entity manager interceptor. */
	private IEntityManagerInterceptor entityManagerInterceptor;

	/**
	 * Create a table by a provided Class.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @return the result status of the create statement execution
	 */
	public int tableCreate(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (columnModel.getGenerated() != null) {
				if (GenerationType.SEQUENCE.name().equals(columnModel.getGenerated())) {
					PersistenceCreateSequenceProcessor persistenceCreateSequenceProcessor = new PersistenceCreateSequenceProcessor(
							getEntityManagerInterceptor());
					persistenceCreateSequenceProcessor.create(connection, tableModel);
				} else if (GenerationType.TABLE.name().equals(columnModel.getGenerated())) {
					PersistenceCreateIdentityProcessor persistenceCreateIdentityProcessor = new PersistenceCreateIdentityProcessor(
							getEntityManagerInterceptor());
					persistenceCreateIdentityProcessor.create(connection, tableModel);
				} else if (GenerationType.IDENTITY.name().equals(columnModel.getGenerated())) {
					// nothing in advance
				} else {
					throw new IllegalArgumentException(format("Generation Type: [{0}] not supported.", columnModel.getGenerated()));
				}
				break;
			}
		}
		PersistenceCreateTableProcessor createTableProcessor = new PersistenceCreateTableProcessor(getEntityManagerInterceptor());
		return createTableProcessor.create(connection, tableModel);
	}

	/**
	 * Drop a table by a provided Class.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @return the result status of the drop statement execution
	 */
	public int tableDrop(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		for (PersistenceTableColumnModel columnModel : tableModel.getColumns()) {
			if (GenerationType.SEQUENCE.name().equals(columnModel.getGenerated())) {
				PersistenceDropSequenceProcessor persistenceDropSequenceProcessor = new PersistenceDropSequenceProcessor(
						getEntityManagerInterceptor());
				persistenceDropSequenceProcessor.drop(connection, tableModel);
				break;
			}
		}
		PersistenceDropTableProcessor dropTableProcessor = new PersistenceDropTableProcessor(getEntityManagerInterceptor());
		return dropTableProcessor.drop(connection, tableModel);
	}

	/**
	 * Check whether a table by a provided Class already exists.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @return true if exists and false otherwise
	 */
	public boolean tableExists(Connection connection, Class<T> clazz) {
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		try {
			return SqlFactory.getNative(connection).exists(connection, tableModel.getTableName());
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Check whether the table already exists in the database and create it if needed.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the clazz
	 */
	public void tableCheck(Connection connection, Class clazz) {
		String id = getConnectionIdentity(connection);
		if (!EXISTING_TABLES_CACHE.contains(id + CONNECTION_ID_SEPARATOR + clazz.getCanonicalName())) {
			if (!tableExists(connection, clazz)) {
				String auto = System.getProperty("DIRIGIBLE_PERSISTENCE_CREATE_TABLE_ON_USE");
				if ((auto != null) && !"true".equals(auto.toLowerCase())) {
					throw new IllegalStateException(
							"The parameter DIRIGIBLE_PERSISTENCE_CREATE_TABLE_ON_USE is off, but the table for the POJO has not been previousely created: "
									+ clazz.getCanonicalName());
				}
				try {
					tableCreate(connection, clazz);
				} catch (Exception e) {
					if (!tableExists(connection, clazz)) {
						throw e;
					}
				}
			}
			EXISTING_TABLES_CACHE.add(id + CONNECTION_ID_SEPARATOR + clazz.getCanonicalName());
		}
	}

	private String getConnectionIdentity(Connection connection) {
		try {
			String url = connection.getMetaData().getURL();
			return url.hashCode() + "";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * Clean up the existing tables cache.
	 */
	public void reset() {
		EXISTING_TABLES_CACHE.clear();
	}

	/**
	 * Insert a single record in the table representing the POJO instance.
	 *
	 * @param connection
	 *            the database connection
	 * @param pojo
	 *            the POJO instance
	 * @return the id of the pojo just inserted
	 */
	public Object insert(Connection connection, Object pojo) {
		tableCheck(connection, pojo.getClass());
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceInsertProcessor insertProcessor = new PersistenceInsertProcessor(getEntityManagerInterceptor());
		return insertProcessor.insert(connection, tableModel, pojo);
	}

	/**
	 * Getter for the single POJO instance.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @param id
	 *            the primary key field's value
	 * @return a POJO instance
	 */
	public T find(Connection connection, Class<T> clazz, Object id) {
		tableCheck(connection, clazz);
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>(getEntityManagerInterceptor());
		return queryProcessor.find(connection, tableModel, clazz, id);
	}

	/**
	 * Getter for the single POJO instance and locks it for update.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @param id
	 *            the primary key field's value
	 * @return a POJO instance
	 */
	public T lock(Connection connection, Class<T> clazz, Object id) {
		tableCheck(connection, clazz);
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>(getEntityManagerInterceptor());
		return queryProcessor.lock(connection, tableModel, clazz, id);
	}

	/**
	 * Getter for all the POJO instances.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @return a list with the POJO instances
	 */
	public List<T> findAll(Connection connection, Class<T> clazz) {
		tableCheck(connection, clazz);
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>(getEntityManagerInterceptor());
		return queryProcessor.findAll(connection, tableModel, clazz);
	}

	/**
	 * Custom query for narrow the search.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @param sql
	 *            the custom SQL script
	 * @param values
	 *            ordered parameters values
	 * @return a list with the POJO instances
	 */
	public List<T> query(Connection connection, Class<T> clazz, String sql, List<Object> values) {
		tableCheck(connection, clazz);
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceQueryProcessor<T> queryProcessor = new PersistenceQueryProcessor<T>(getEntityManagerInterceptor());
		return queryProcessor.query(connection, tableModel, clazz, sql, values);
	}

	/**
	 * Custom query for narrow the search.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @param sql
	 *            the custom SQL script
	 * @param values
	 *            ordered parameters values
	 * @return a list with the POJO instances
	 */
	public List<T> query(Connection connection, Class<T> clazz, String sql, Object... values) {
		return query(connection, clazz, sql, Arrays.asList(values));
	}

	/**
	 * Custom update statement.
	 *
	 * @param connection
	 *            the database connection
	 * @param sql
	 *            the custom SQL script
	 * @param values
	 *            ordered parameters values
	 * @return a list with the POJO instances
	 */
	public int execute(Connection connection, String sql, List<Object> values) {
		PersistenceExecuteProcessor<T> executeProcessor = new PersistenceExecuteProcessor<T>(getEntityManagerInterceptor());
		return executeProcessor.execute(connection, sql, values);
	}

	/**
	 * Custom update statement.
	 *
	 * @param connection
	 *            the database connection
	 * @param sql
	 *            the custom SQL script
	 * @param values
	 *            ordered parameters values
	 * @return a list with the POJO instances
	 */
	public int execute(Connection connection, String sql, Object... values) {
		return execute(connection, sql, Arrays.asList(values));
	}

	/**
	 * Delete a single record representing a single POJO instance.
	 *
	 * @param connection
	 *            the database connection
	 * @param clazz
	 *            the POJO's Class
	 * @param id
	 *            the primary key field's value
	 * @return the result status of the delete statement execution
	 */
	public int delete(Connection connection, Class<T> clazz, Object id) {
		tableCheck(connection, clazz);
		PersistenceTableModel tableModel = PersistenceFactory.createModel(clazz);
		PersistenceDeleteProcessor<T> deleteProcessor = new PersistenceDeleteProcessor<T>(getEntityManagerInterceptor());
		return deleteProcessor.delete(connection, tableModel, clazz, id);
	}

	/**
	 * Update.
	 *
	 * @param connection
	 *            the database connection
	 * @param pojo
	 *            the POJO instance
	 * @param id
	 *            the primary key field's value
	 * @return the result status of the update statement execution
	 */
	public int update(Connection connection, Object pojo, Object id) {
		tableCheck(connection, pojo.getClass());
		PersistenceTableModel tableModel = PersistenceFactory.createModel(pojo);
		PersistenceUpdateProcessor<T> updateProcessor = new PersistenceUpdateProcessor<T>(getEntityManagerInterceptor());
		return updateProcessor.update(connection, tableModel, pojo, id);
	}

	/**
	 * Gets the entity manager interceptor.
	 *
	 * @return the entity manager interceptor
	 */
	public IEntityManagerInterceptor getEntityManagerInterceptor() {
		return entityManagerInterceptor;
	}

	/**
	 * Sets the entity manager interceptor.
	 *
	 * @param entityManagerInterceptor
	 *            the new entity manager interceptor
	 */
	public void setEntityManagerInterceptor(IEntityManagerInterceptor entityManagerInterceptor) {
		this.entityManagerInterceptor = entityManagerInterceptor;
	}

}
