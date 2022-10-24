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
package org.eclipse.dirigible.components.data.structures.synchronizer;

import static java.text.MessageFormat.format;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.components.base.artefact.Artefact;
import org.eclipse.dirigible.components.base.artefact.ArtefactLifecycle;
import org.eclipse.dirigible.components.base.artefact.ArtefactService;
import org.eclipse.dirigible.components.base.artefact.ArtefactState;
import org.eclipse.dirigible.components.base.artefact.topology.TopologyWrapper;
import org.eclipse.dirigible.components.base.synchronizer.Synchronizer;
import org.eclipse.dirigible.components.base.synchronizer.SynchronizerCallback;
import org.eclipse.dirigible.components.data.structures.domain.Table;
import org.eclipse.dirigible.components.data.structures.domain.TableLifecycle;
import org.eclipse.dirigible.components.data.structures.service.TableService;
import org.eclipse.dirigible.components.database.DataSourcesManager;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The Class TableSynchronizer.
 *
 * @param A the generic type
 */
@Component
@Order(20)
public class TablesSynchronizer<A extends Artefact> implements Synchronizer<Table> {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TablesSynchronizer.class);
	
	/** The Constant FILE_EXTENSION_TABLE. */
	public static final String FILE_EXTENSION_TABLE = ".table";
	
	/** The table service. */
	private TableService tableService;
	
	private DataSourcesManager dataSourcesManager;
	
	/** The synchronization callback. */
	private SynchronizerCallback callback;
	
	/**
	 * Instantiates a new table synchronizer.
	 *
	 * @param tableService the table service
	 */
	@Autowired
	public TablesSynchronizer(TableService tableService, DataSourcesManager dataSourcesManager) {
		this.tableService = tableService;
		this.dataSourcesManager = dataSourcesManager;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	@Override
	public ArtefactService<Table> getService() {
		return tableService;
	}

	/**
	 * Checks if is accepted.
	 *
	 * @param file the file
	 * @param attrs the attrs
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(Path file, BasicFileAttributes attrs) {
		return file.toString().endsWith(FILE_EXTENSION_TABLE);
	}

	/**
	 * Checks if is accepted.
	 *
	 * @param type the type
	 * @return true, if is accepted
	 */
	@Override
	public boolean isAccepted(String type) {
		return Table.ARTEFACT_TYPE.equals(type);
	}

	/**
	 * Load.
	 *
	 * @param location the location
	 * @param content the content
	 * @return the list
	 */
	@Override
	public List load(String location, byte[] content) {
		Table table = GsonHelper.GSON.fromJson(new String(content, StandardCharsets.UTF_8), Table.class);
		table.setLocation(location);
		table.setType(Table.ARTEFACT_TYPE);
		table.updateKey();
		table.getColumns().forEach(c -> c.setTable(table));
		if (table.getIndexes() != null) {
			table.getIndexes().forEach(i -> i.setTable(table));
		}
		if (table.getConstraints() != null) {
			table.getConstraints().setTable(table);
			if (table.getConstraints().getPrimaryKey() != null) {
				table.getConstraints().getPrimaryKey().setConstraints(table.getConstraints());
			}
			if (table.getConstraints().getForeignKeys() != null) {
				table.getConstraints().getForeignKeys().forEach(fk -> fk.setConstraints(table.getConstraints()));
			}
			if (table.getConstraints().getUniqueIndexes() != null) {
				table.getConstraints().getUniqueIndexes().forEach(u -> u.setConstraints(table.getConstraints()));
			}
			if (table.getConstraints().getChecks() != null) {
				table.getConstraints().getChecks().forEach(c -> c.setConstraints(table.getConstraints()));
			}
		}
		
		try {
			getService().save(table);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			if (logger.isErrorEnabled()) {logger.error("table: {}", table);}
			if (logger.isErrorEnabled()) {logger.error("content: {}", new String(content));}
		}
		return List.of(table);
	}

	/**
	 * Prepare.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void prepare(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		// drop tables' foreign keys in a reverse order
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, TableLifecycle.FOREIGN_KEYS_DROP.toString());
			callback.registerErrors(this, results, TableLifecycle.FOREIGN_KEYS_DROP.toString(), ArtefactState.FAILED_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
		
		// drop tables in a reverse order
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, TableLifecycle.DROP.toString());
			callback.registerErrors(this, results, TableLifecycle.DROP.toString(), ArtefactState.FAILED_DELETE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
	}
	
	/**
	 * Process.
	 *
	 * @param wrappers the wrappers
	 * @param depleter the depleter
	 */
	@Override
	public void process(List<TopologyWrapper<? extends Artefact>> wrappers, TopologicalDepleter<TopologyWrapper<? extends Artefact>> depleter) {
		
		// process tables
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, TableLifecycle.CREATE.toString());
			callback.registerErrors(this, results, TableLifecycle.CREATE.toString(), ArtefactState.FAILED_CREATE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
		
		// process tables foreign keys
		try {
			List<TopologyWrapper<? extends Artefact>> results = depleter.deplete(wrappers, TableLifecycle.FOREIGN_KEYS_CREATE.toString());
			callback.registerErrors(this, results, TableLifecycle.FOREIGN_KEYS_CREATE.toString(), ArtefactState.FAILED_CREATE);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
		
	}

	/**
	 * Complete.
	 *
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(TopologyWrapper<Artefact> wrapper, String flow) {
		
		try (Connection connection = dataSourcesManager.getDefaultDataSource().getConnection()) {
		
			Table table = null;
			if (wrapper.getArtefact() instanceof Table) {
				table = (Table) wrapper.getArtefact();
			} else {
				throw new UnsupportedOperationException(String.format("Trying to process %s as Table", wrapper.getArtefact().getClass()));
			}
			
			TableLifecycle flag = TableLifecycle.valueOf(flow);
			switch (flag) {
			case UPDATE:
				executeTableUpdate(connection, table);
				break;
			case CREATE:
				if (!SqlFactory.getNative(connection).exists(connection, table.getName())) {
					executeTableCreate(connection, table);
					callback.registerState(this, wrapper, ArtefactLifecycle.CREATED.toString(), ArtefactState.SUCCESSFUL_CREATE);
				} else {
					if (logger.isWarnEnabled()) {logger.warn(format("Table [{0}] already exists during the update process", table.getName()));}
					if (SqlFactory.getNative(connection).count(connection, table.getName()) != 0) {
						executeTableAlter(connection, table);
						callback.registerState(this, wrapper, ArtefactLifecycle.UPDATED.toString(), ArtefactState.SUCCESSFUL_UPDATE);
					}
				}
				break;
			case FOREIGN_KEYS_CREATE:
				executeTableForeignKeysCreate(connection, table);
				break;
			case ALTER:
				executeTableAlter(connection, table);
				break;
			case DROP:
				if (SqlFactory.getNative(connection).exists(connection, table.getName())) {
					if (SqlFactory.getNative(connection).count(connection, table.getName()) == 0) {
						executeTableDrop(connection, table);
					} else {
						String message = format("Table [{1}] cannot be deleted during the update process, because it is not empty", table.getName());
						if (logger.isWarnEnabled()) {logger.warn(message);}
						callback.registerState(this, wrapper, ArtefactLifecycle.DELETED.toString(), ArtefactState.FAILED);
					}
				}
				break;
			case FOREIGN_KEYS_DROP:
				if (SqlFactory.getNative(connection).exists(connection, table.getName())) {
					executeTableForeignKeysDrop(connection, table);
				}
				break;
			default:
				throw new UnsupportedOperationException(flow);
			}
			return true;
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
			return false;
		}
	}

	/**
	 * Cleanup.
	 *
	 * @param table the extension point
	 */
	@Override
	public void cleanup(Table table) {
		try {
			getService().delete(table);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {logger.error(e.getMessage(), e);}
			callback.addError(e.getMessage());
		}
	}
	
	@Override
	public void setCallback(SynchronizerCallback callback) {
		this.callback = callback;
	}
	
	/**
	 * Execute table update.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeTableUpdate(Connection connection, Table tableModel) throws SQLException {
		if (logger.isInfoEnabled()) {logger.info("Processing Update Table: " + tableModel.getName());}
		if (SqlFactory.getNative(connection).exists(connection, tableModel.getName())) {
			if (SqlFactory.getNative(connection).count(connection, tableModel.getName()) == 0) {
				executeTableDrop(connection, tableModel);
				executeTableCreate(connection, tableModel);
			} else {
				executeTableAlter(connection, tableModel);
			}
		} else {
			executeTableCreate(connection, tableModel);
		}
	}

	/**
	 * Execute table create.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeTableCreate(Connection connection, Table tableModel) throws SQLException {
		TableCreateProcessor.execute(connection, tableModel, true);
	}
	
	/**
	 * Execute table foreign keys create.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeTableForeignKeysCreate(Connection connection, Table tableModel) throws SQLException {
		TableForeignKeysCreateProcessor.execute(connection, tableModel);
	}

	/**
	 * Execute table alter.
	 *
	 * @param connection            the connection
	 * @param tableModel            the table model
	 * @throws SQLException the SQL exception
	 */
	public void executeTableAlter(Connection connection, Table tableModel) throws SQLException {
		TableAlterProcessor.execute(connection, tableModel);
	}

	/**
	 * Execute table drop.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeTableDrop(Connection connection, Table tableModel) throws SQLException {
		TableDropProcessor.execute(connection, tableModel);
	}
	
	/**
	 * Execute table foreign keys drop.
	 *
	 * @param connection
	 *            the connection
	 * @param tableModel
	 *            the table model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeTableForeignKeysDrop(Connection connection, Table tableModel) throws SQLException {
		TableForeignKeysDropProcessor.execute(connection, tableModel);
	}

}
