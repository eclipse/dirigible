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
package org.eclipse.dirigible.database.ds.synchronizer;

import static java.text.MessageFormat.format;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.commons.api.artefacts.IArtefactDefinition;
import org.eclipse.dirigible.commons.api.topology.ITopologicallyDepletable;
import org.eclipse.dirigible.commons.api.topology.ITopologicallySortable;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.database.ds.artefacts.TableSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.model.DataStructureDependencyModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TopologyDataStructureModelWrapper.
 */
public class TopologyDataStructureModelWrapper implements ITopologicallySortable, ITopologicallyDepletable {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(TopologyDataStructureModelWrapper.class);
	
	/** The Constant TABLE_ARTEFACT. */
	private static final TableSynchronizationArtefactType TABLE_ARTEFACT = new TableSynchronizationArtefactType();
	
	/** The synchronizer. */
	private DataStructuresSynchronizer synchronizer;
	
	/** The connection. */
	private Connection connection;
	
	/** The model. */
	private DataStructureModel model;
	
	/** The wrappers. */
	private Map<String, TopologyDataStructureModelWrapper> wrappers;

	/**
	 * Instantiates a new topology data structure model wrapper.
	 *
	 * @param synchronizer the synchronizer
	 * @param connection the connection
	 * @param model the model
	 * @param wrappers the wrappers
	 */
	public TopologyDataStructureModelWrapper(DataStructuresSynchronizer synchronizer, Connection connection, DataStructureModel model, Map<String, TopologyDataStructureModelWrapper> wrappers) {
		this.synchronizer = synchronizer;
		this.connection = connection;
		this.model = model;
		this.wrappers = wrappers;
		this.wrappers.put(getId(), this);
	}
	
	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public DataStructureModel getModel() {
		return model;
	}
	
	/**
	 * Gets the synchronizer.
	 *
	 * @return the synchronizer
	 */
	public DataStructuresSynchronizer getSynchronizer() {
		return synchronizer;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.model.getName();
	}

	/**
	 * Gets the dependencies.
	 *
	 * @return the dependencies
	 */
	@Override
	public List<ITopologicallySortable> getDependencies() {
		List<ITopologicallySortable> dependencies = new ArrayList<ITopologicallySortable>();
		for (DataStructureDependencyModel dependency: this.model.getDependencies()) {
			String dependencyName = dependency.getName();
			if (!wrappers.containsKey(dependencyName)) {
				if (logger.isWarnEnabled()) {logger.warn("Dependency is not present in this cycle: " + dependencyName);}
			} else {
				dependencies.add(wrappers.get(dependencyName));
			}
		}
		return dependencies;
	}
	
	/**
	 * Complete.
	 *
	 * @param flow the flow
	 * @return true, if successful
	 */
	@Override
	public boolean complete(String flow) {
		try {
			TopologyDataStructureModelEnum flag = TopologyDataStructureModelEnum.valueOf(flow);
			switch (flag) {
			case EXECUTE_TABLE_UPDATE:
				if (model instanceof DataStructureTableModel) {
					executeTableUpdate(connection, (DataStructureTableModel) this.model);
				}
				break;
			case EXECUTE_TABLE_CREATE:
				if (model instanceof DataStructureTableModel) {
					if (!SqlFactory.getNative(connection).exists(connection, this.model.getName())) {
						executeTableCreate(connection, (DataStructureTableModel) model);
						applyArtefactState(this.model, TABLE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
					} else {
						if (logger.isWarnEnabled()) {logger.warn(format("Table [{0}] already exists during the update process", this.model.getName()));}
						if (SqlFactory.getNative(connection).count(connection, model.getName()) != 0) {
							executeTableAlter(connection, (DataStructureTableModel) model);
							applyArtefactState(this.model, TABLE_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
						}
					}
				}
				break;
			case EXECUTE_TABLE_FOREIGN_KEYS_CREATE:
				if (model instanceof DataStructureTableModel) {
					executeTableForeignKeysCreate(connection, (DataStructureTableModel) this.model);
				}
				break;
			case EXECUTE_TABLE_ALTER:
				if (model instanceof DataStructureTableModel) {
					executeTableAlter(connection, (DataStructureTableModel) this.model);
				}
				break;
			case EXECUTE_TABLE_DROP:
				if (model instanceof DataStructureTableModel) {
					if (SqlFactory.getNative(connection).exists(connection, this.model.getName())) {
						if (SqlFactory.getNative(connection).count(connection, this.model.getName()) == 0) {
							executeTableDrop(connection, (DataStructureTableModel) this.model);
						} else {
							String message = format("Table [{1}] cannot be deleted during the update process, because it is not empty", this.model.getName());
							if (logger.isWarnEnabled()) {logger.warn(message);}
							applyArtefactState(this.model, TABLE_ARTEFACT, ArtefactState.FAILED, message);
						}
					}
				}
				break;
			case EXECUTE_TABLE_FOREIGN_KEYS_DROP:
				if (model instanceof DataStructureTableModel) {
					if (SqlFactory.getNative(connection).exists(connection, this.model.getName())) {
						executeTableForeignKeysDrop(connection, (DataStructureTableModel) this.model);
					}
				}
				break;
			case EXECUTE_VIEW_CREATE:
				if (model instanceof DataStructureViewModel) {
					executeViewCreate(connection, (DataStructureViewModel) this.model);
				}
				break;
			case EXECUTE_VIEW_DROP:
				if (model instanceof DataStructureViewModel) {
					executeViewDrop(connection, (DataStructureViewModel) this.model);
				}
				break;
				
			default:
				throw new UnsupportedOperationException(flow);
			}
			return true;
		} catch (SQLException e) {
			if (logger.isWarnEnabled()) {logger.warn("Failed on trying to complete the artefact: " + e.getMessage());}
			return false;
		}
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
	public void executeTableUpdate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableUpdate(connection, tableModel);
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
	private void executeTableCreate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableCreate(connection, tableModel);
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
	public void executeTableForeignKeysCreate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableForeignKeysCreate(connection, tableModel);
	}

	/**
	 * Execute table alter.
	 *
	 * @param connection            the connection
	 * @param tableModel            the table model
	 * @throws SQLException the SQL exception
	 */
	private void executeTableAlter(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableAlter(connection, tableModel);
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
	public void executeTableDrop(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableDrop(connection, tableModel);
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
	private void executeTableForeignKeysDrop(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		this.synchronizer.executeTableForeignKeysDrop(connection, tableModel);
	}

	/**
	 * Execute view create.
	 *
	 * @param connection
	 *            the connection
	 * @param viewModel
	 *            the view model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeViewCreate(Connection connection, DataStructureViewModel viewModel) throws SQLException {
		this.synchronizer.executeViewCreate(connection, viewModel);
	}

	/**
	 * Execute view drop.
	 *
	 * @param connection
	 *            the connection
	 * @param viewModel
	 *            the view model
	 * @throws SQLException
	 *             the SQL exception
	 */
	public void executeViewDrop(Connection connection, DataStructureViewModel viewModel) throws SQLException {
		this.synchronizer.executeViewDrop(connection, viewModel);
	}
	
	/**
	 * Apply the state.
	 *
	 * @param artefact the artefact
	 * @param type the type
	 * @param state the state
	 */
	public void applyArtefactState(IArtefactDefinition artefact, ISynchronizerArtefactType type, ISynchronizerArtefactType.ArtefactState state) {
		applyArtefactState(artefact, type, state, null);
	}

	/**
	 * Apply the state.
	 *
	 * @param artefact the artefact
	 * @param type the type
	 * @param state the state
	 * @param message the message
	 */
	public void applyArtefactState(IArtefactDefinition artefact, ISynchronizerArtefactType type, ISynchronizerArtefactType.ArtefactState state, String message) {
		this.synchronizer.applyArtefactState(artefact, type, state);
	}

}
