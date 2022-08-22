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
package org.eclipse.dirigible.database.ds.synchronizer;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.problems.IProblemsConstants;
import org.eclipse.dirigible.api.v3.problems.ProblemsFacade;
import org.eclipse.dirigible.commons.api.helpers.DataStructuresUtils;
import org.eclipse.dirigible.commons.api.topology.TopologicalDepleter;
import org.eclipse.dirigible.commons.api.topology.TopologicalSorter;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.problems.exceptions.ProblemsException;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.IOrderedSynchronizerContribution;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.artefacts.AppendSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.DeleteSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.ReplaceSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.SchemaSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.TableSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.UpdateSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.artefacts.ViewSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.model.DataStructureDataAppendModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataDeleteModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataReplaceModel;
import org.eclipse.dirigible.database.ds.model.DataStructureDataUpdateModel;
import org.eclipse.dirigible.database.ds.model.DataStructureModel;
import org.eclipse.dirigible.database.ds.model.DataStructureSchemaModel;
import org.eclipse.dirigible.database.ds.model.DataStructureTableModel;
import org.eclipse.dirigible.database.ds.model.DataStructureViewModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.ds.model.processors.TableAlterProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableDropProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableForeignKeysCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.TableForeignKeysDropProcessor;
import org.eclipse.dirigible.database.ds.model.processors.ViewCreateProcessor;
import org.eclipse.dirigible.database.ds.model.processors.ViewDropProcessor;
import org.eclipse.dirigible.database.ds.model.transfer.TableDataReader;
import org.eclipse.dirigible.database.ds.model.transfer.TableExporter;
import org.eclipse.dirigible.database.ds.model.transfer.TableImporter;
import org.eclipse.dirigible.database.ds.model.transfer.TableMetadataHelper;
import org.eclipse.dirigible.database.ds.service.DataStructuresCoreService;
import org.eclipse.dirigible.database.persistence.PersistenceException;
import org.eclipse.dirigible.database.persistence.PersistenceManager;
import org.eclipse.dirigible.database.persistence.processors.identity.Identity;
import org.eclipse.dirigible.database.sql.SqlFactory;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Data Structures Synchronizer.
 */
public class DataStructuresSynchronizer extends AbstractSynchronizer implements IOrderedSynchronizerContribution {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DataStructuresSynchronizer.class);

	/** The Constant TABLES_PREDELIVERED. */
	private static final Map<String, DataStructureTableModel> TABLES_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureTableModel>());

	/** The Constant VIEWS_PREDELIVERED. */
	private static final Map<String, DataStructureViewModel> VIEWS_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureViewModel>());

	/** The Constant REPLACE_PREDELIVERED. */
	private static final Map<String, DataStructureDataReplaceModel> REPLACE_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureDataReplaceModel>());

	/** The Constant APPEND_PREDELIVERED. */
	private static final Map<String, DataStructureDataAppendModel> APPEND_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureDataAppendModel>());

	/** The Constant DELETE_PREDELIVERED. */
	private static final Map<String, DataStructureDataDeleteModel> DELETE_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureDataDeleteModel>());

	/** The Constant UPDATE_PREDELIVERED. */
	private static final Map<String, DataStructureDataUpdateModel> UPDATE_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureDataUpdateModel>());
	
	/** The Constant SCHEMA_PREDELIVERED. */
	private static final Map<String, DataStructureSchemaModel> SCHEMA_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureSchemaModel>());

	/** The Constant TABLES_SYNCHRONIZED. */
	private static final List<String> TABLES_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant VIEWS_SYNCHRONIZED. */
	private static final List<String> VIEWS_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant REPLACE_SYNCHRONIZED. */
	private static final List<String> REPLACE_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant APPEND_SYNCHRONIZED. */
	private static final List<String> APPEND_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant DELETE_SYNCHRONIZED. */
	private static final List<String> DELETE_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant UPDATE_SYNCHRONIZED. */
	private static final List<String> UPDATE_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	/** The Constant SCHEMA_SYNCHRONIZED. */
	private static final List<String> SCHEMA_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	/** The Constant DATA_STRUCTURE_MODELS. */
	private static final Map<String, DataStructureModel> DATA_STRUCTURE_MODELS = new LinkedHashMap<String, DataStructureModel>();

	/** The Constant DATA_STRUCTURE_REPLACE_MODELS. */
	private static final Map<String, DataStructureDataReplaceModel> DATA_STRUCTURE_REPLACE_MODELS = new LinkedHashMap<String, DataStructureDataReplaceModel>();

	/** The Constant DATA_STRUCTURE_APPEND_MODELS. */
	private static final Map<String, DataStructureDataAppendModel> DATA_STRUCTURE_APPEND_MODELS = new LinkedHashMap<String, DataStructureDataAppendModel>();

	/** The Constant DATA_STRUCTURE_DELETE_MODELS. */
	private static final Map<String, DataStructureDataDeleteModel> DATA_STRUCTURE_DELETE_MODELS = new LinkedHashMap<String, DataStructureDataDeleteModel>();

	/** The Constant DATA_STRUCTURE_UPDATE_MODELS. */
	private static final Map<String, DataStructureDataUpdateModel> DATA_STRUCTURE_UPDATE_MODELS = new LinkedHashMap<String, DataStructureDataUpdateModel>();
	
	/** The Constant DATA_STRUCTURE_SCHEMA_MODELS. */
	private static final Map<String, DataStructureSchemaModel> DATA_STRUCTURE_SCHEMA_MODELS = new LinkedHashMap<String, DataStructureSchemaModel>();

	/** The data structures core service. */
	private DataStructuresCoreService dataStructuresCoreService = new DataStructuresCoreService();
	
	/** The data source. */
	private DataSource dataSource = null;
	
	/** The synchronizer name. */
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();
	
	/** The Constant SCHEMA_ARTEFACT. */
	private static final SchemaSynchronizationArtefactType SCHEMA_ARTEFACT = new SchemaSynchronizationArtefactType();
	
	/** The Constant TABLE_ARTEFACT. */
	private static final TableSynchronizationArtefactType TABLE_ARTEFACT = new TableSynchronizationArtefactType();
	
	/** The Constant VIEW_ARTEFACT. */
	private static final ViewSynchronizationArtefactType VIEW_ARTEFACT = new ViewSynchronizationArtefactType();
	
	/** The Constant REPLACE_ARTEFACT. */
	private static final ReplaceSynchronizationArtefactType REPLACE_ARTEFACT = new ReplaceSynchronizationArtefactType();
	
	/** The Constant APPEND_ARTEFACT. */
	private static final AppendSynchronizationArtefactType APPEND_ARTEFACT = new AppendSynchronizationArtefactType();
	
	/** The Constant DELETE_ARTEFACT. */
	private static final DeleteSynchronizationArtefactType DELETE_ARTEFACT = new DeleteSynchronizationArtefactType();
	
	/** The Constant UPDATE_ARTEFACT. */
	private static final UpdateSynchronizationArtefactType UPDATE_ARTEFACT = new UpdateSynchronizationArtefactType();
	
	/**
	 * Gets the data source.
	 *
	 * @return the data source
	 */
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		}
		return dataSource;
	}
	
	/**
	 * Synchronize.
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (DataStructuresSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Data Structures...");
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						updateDatabaseSchema();
						updateDatabaseContent();
						int immutableTablesCount = TABLES_PREDELIVERED.size();
						int immutableViewsCount = VIEWS_PREDELIVERED.size();
						int immutableSchemaCount = SCHEMA_PREDELIVERED.size();
						int immutableReplaceCount = REPLACE_PREDELIVERED.size();
						int immutableAppendCount = APPEND_PREDELIVERED.size();
						int immutableDeleteCount = DELETE_PREDELIVERED.size();
						int immutableUpdateCount = UPDATE_PREDELIVERED.size();
						
						int mutableTablesCount = TABLES_SYNCHRONIZED.size();
						int mutableViewsCount = VIEWS_SYNCHRONIZED.size();
						int mutableSchemaCount = DATA_STRUCTURE_SCHEMA_MODELS.size();
						int mutableReplaceCount = DATA_STRUCTURE_REPLACE_MODELS.size();
						int mutableAppendCount = DATA_STRUCTURE_APPEND_MODELS.size();
						int mutableDeleteCount = DATA_STRUCTURE_DELETE_MODELS.size();
						int mutableUpdateCount = DATA_STRUCTURE_UPDATE_MODELS.size();
						
						cleanup(); // TODO drop tables and views for non-existing models
						clearCache();
						
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: [ Tables: {0}, Views: {1}, Schema: {2}, Replace: {3}, Append: {4}, Delete: {5}, Update: {6}], "
								+ "Mutable: [Tables: {7}, Views: {8}, Schema: {9}, Replace: {10}, Append: {11}, Delete: {12}, Update: {13}]", 
								immutableTablesCount, immutableViewsCount, immutableSchemaCount, immutableReplaceCount, immutableAppendCount, immutableDeleteCount, immutableUpdateCount,
								mutableTablesCount, mutableViewsCount, mutableSchemaCount, mutableReplaceCount, mutableAppendCount, mutableDeleteCount, mutableUpdateCount));
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Data Structures failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Data Structures files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Data Structures.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		DataStructuresSynchronizer synchronizer = new DataStructuresSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register predelivered table.
	 *
	 * @param tableModelPath
	 *            the table model path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredTable(String tableModelPath) throws IOException {
		String json = loadResourceContent(tableModelPath);
		DataStructureTableModel tableModel = dataStructuresCoreService.parseTable(json);
		tableModel.setLocation(tableModelPath);
		TABLES_PREDELIVERED.put(tableModelPath, tableModel);
	}

	/**
	 * Register predelivered view.
	 *
	 * @param viewModelPath
	 *            the view model path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredView(String viewModelPath) throws IOException {
		String json = loadResourceContent(viewModelPath);
		DataStructureViewModel viewModel = dataStructuresCoreService.parseView(json);
		viewModel.setLocation(viewModelPath);
		VIEWS_PREDELIVERED.put(viewModelPath, viewModel);
	}

	/**
	 * Register predelivered replace files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredReplace(String contentPath) throws IOException {
		String data = loadResourceContent(contentPath);
		DataStructureDataReplaceModel model = dataStructuresCoreService.parseReplace(contentPath, data);
		REPLACE_PREDELIVERED.put(contentPath, model);
	}

	/**
	 * Register predelivered append files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredAppend(String contentPath) throws IOException {
		String data = loadResourceContent(contentPath);
		DataStructureDataAppendModel model = dataStructuresCoreService.parseAppend(contentPath, data);
		APPEND_PREDELIVERED.put(contentPath, model);
	}

	/**
	 * Register predelivered delete files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredDelete(String contentPath) throws IOException {
		String data = loadResourceContent(contentPath);
		DataStructureDataDeleteModel model = dataStructuresCoreService.parseDelete(contentPath, data);
		DELETE_PREDELIVERED.put(contentPath, model);
	}

	/**
	 * Register predelivered update files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredUpdate(String contentPath) throws IOException {
		String data = loadResourceContent(contentPath);
		DataStructureDataUpdateModel model = dataStructuresCoreService.parseUpdate(contentPath, data);
		UPDATE_PREDELIVERED.put(contentPath, model);
	}
	
	/**
	 * Register predelivered schema files.
	 *
	 * @param contentPath
	 *            the data path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredSchema(String contentPath) throws IOException {
		String data = loadResourceContent(contentPath);
		DataStructureSchemaModel model = dataStructuresCoreService.parseSchema(contentPath, data);
		SCHEMA_PREDELIVERED.put(contentPath, model);
	}

	/**
	 * Load resource content.
	 *
	 * @param modelPath the model path
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String loadResourceContent(String modelPath) throws IOException {
		InputStream in = DataStructuresSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + modelPath);
		try {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			return content;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Clear cache.
	 */
	private void clearCache() {
		TABLES_SYNCHRONIZED.clear();
		VIEWS_SYNCHRONIZED.clear();
		DATA_STRUCTURE_MODELS.clear();
		DATA_STRUCTURE_REPLACE_MODELS.clear();
		DATA_STRUCTURE_APPEND_MODELS.clear();
		DATA_STRUCTURE_DELETE_MODELS.clear();
		DATA_STRUCTURE_UPDATE_MODELS.clear();
		DATA_STRUCTURE_SCHEMA_MODELS.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Data Structures...");
		// Tables
		for (DataStructureTableModel tableModel : TABLES_PREDELIVERED.values()) {
			try {
				synchronizeTable(tableModel);
			} catch (Exception e) {
				logger.error(format("Table [{0}] skipped due to an error: {1}", tableModel.getLocation(), e.getMessage()), e);
			}
		}
		// Views
		for (DataStructureViewModel viewModel : VIEWS_PREDELIVERED.values()) {
			try {
				synchronizeView(viewModel);
			} catch (Exception e) {
				logger.error(format("View [{0}] skipped due to an error: {1}", viewModel.getLocation(), e.getMessage()), e);
			}
		}
		// Replace
		for (DataStructureDataReplaceModel data : REPLACE_PREDELIVERED.values()) {
			try {
				synchronizeReplace(data);
			} catch (Exception e) {
				logger.error(format("Replace data [{0}] skipped due to an error: {1}", data, e.getMessage()), e);
			}
		}
		// Append
		for (DataStructureDataAppendModel data : APPEND_PREDELIVERED.values()) {
			try {
				synchronizeAppend(data);
			} catch (Exception e) {
				logger.error(format("Append data [{0}] skipped due to an error: {1}", data, e.getMessage()), e);
			}
		}
		// Delete
		for (DataStructureDataDeleteModel data : DELETE_PREDELIVERED.values()) {
			try {
				synchronizeDelete(data);
			} catch (Exception e) {
				logger.error(format("Delete data [{0}] skipped due to an error: {1}", data, e.getMessage()), e);
			}
		}
		// Update
		for (DataStructureDataUpdateModel data : UPDATE_PREDELIVERED.values()) {
			try {
				synchronizeUpdate(data);
			} catch (Exception e) {
				logger.error(format("Update data [{0}] skipped due to an error: {1}", data, e.getMessage()), e);
			}
		}
		// Schema
		for (DataStructureSchemaModel schema : SCHEMA_PREDELIVERED.values()) {
			try {
				synchronizeSchema(schema);
			} catch (Exception e) {
				logger.error(format("Update schema [{0}] skipped due to an error: {1}", schema, e.getMessage()), e);
			}
		}
		logger.trace("Done synchronizing predelivered Data Structures.");
	}

	/**
	 * Synchronize table.
	 *
	 * @param tableModel
	 *            the table model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeTable(DataStructureTableModel tableModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsTable(tableModel.getLocation())) {
				DataStructureTableModel duplicated = dataStructuresCoreService.getTableByName(tableModel.getName());
				if (duplicated != null) {
					String message = format("Table [{0}] defined by the model at: [{1}] has already been defined by the model at: [{2}]", tableModel.getName(),
							tableModel.getLocation(), duplicated.getLocation());
					applyArtefactState(tableModel, TABLE_ARTEFACT, ArtefactState.FATAL, message);
					logProblem(message, ERROR_TYPE, tableModel.getLocation(), TABLE_ARTEFACT.getId());
					throw new SynchronizationException(message);
				}
				dataStructuresCoreService.createTable(tableModel.getLocation(), tableModel.getName(), tableModel.getHash());
				DATA_STRUCTURE_MODELS.put(tableModel.getName(), tableModel);
				logger.info("Synchronized a new Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
				applyArtefactState(tableModel, TABLE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				DataStructureTableModel existing = dataStructuresCoreService.getTable(tableModel.getLocation());
				if (!tableModel.equals(existing)) {
					dataStructuresCoreService.updateTable(tableModel.getLocation(), tableModel.getName(), tableModel.getHash());
					DATA_STRUCTURE_MODELS.put(tableModel.getName(), tableModel);
					logger.info("Synchronized a modified Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
					applyArtefactState(tableModel, TABLE_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			TABLES_SYNCHRONIZED.add(tableModel.getLocation());
		} catch (DataStructuresException e) {
			applyArtefactState(tableModel, TABLE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, tableModel.getLocation(), TABLE_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize view.
	 *
	 * @param viewModel
	 *            the view model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeView(DataStructureViewModel viewModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsView(viewModel.getLocation())) {
				DataStructureViewModel duplicated = dataStructuresCoreService.getViewByName(viewModel.getName());
				if (duplicated != null) {
					throw new SynchronizationException(
							format("View [{0}] defined by the model at: [{1}] has already been defined by the model at: [{2}]", viewModel.getName(),
									viewModel.getLocation(), duplicated.getLocation()));
				}
				dataStructuresCoreService.createView(viewModel.getLocation(), viewModel.getName(), viewModel.getHash());
				DATA_STRUCTURE_MODELS.put(viewModel.getName(), viewModel);
				logger.info("Synchronized a new View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
				applyArtefactState(viewModel, VIEW_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				DataStructureViewModel existing = dataStructuresCoreService.getView(viewModel.getLocation());
				if (!viewModel.equals(existing)) {
					dataStructuresCoreService.updateView(viewModel.getLocation(), viewModel.getName(), viewModel.getHash());
					DATA_STRUCTURE_MODELS.put(viewModel.getName(), viewModel);
					logger.info("Synchronized a modified View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
					applyArtefactState(viewModel, VIEW_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			VIEWS_SYNCHRONIZED.add(viewModel.getLocation());
		} catch (DataStructuresException e) {
			applyArtefactState(viewModel, VIEW_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, viewModel.getLocation(), VIEW_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize replace.
	 *
	 * @param dataModel
	 *            the data model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeReplace(DataStructureDataReplaceModel dataModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsReplace(dataModel.getLocation())) {
				dataStructuresCoreService.createReplace(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
				DATA_STRUCTURE_REPLACE_MODELS.put(dataModel.getName(), dataModel);
				logger.info("Synchronized a new Replace Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
			} else {
				DataStructureDataReplaceModel existing = dataStructuresCoreService.getReplace(dataModel.getLocation());
				if (!dataModel.equals(existing)) {
					dataStructuresCoreService.updateReplace(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
					DATA_STRUCTURE_REPLACE_MODELS.put(dataModel.getName(), dataModel);
					logger.info("Synchronized a modified Replace Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
				}
			}
			REPLACE_SYNCHRONIZED.add(dataModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize append.
	 *
	 * @param dataModel
	 *            the data model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeAppend(DataStructureDataAppendModel dataModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsAppend(dataModel.getLocation())) {
				dataStructuresCoreService.createAppend(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
				DATA_STRUCTURE_APPEND_MODELS.put(dataModel.getName(), dataModel);
				logger.info("Synchronized a new Append Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
			} else {
				DataStructureDataAppendModel existing = dataStructuresCoreService.getAppend(dataModel.getLocation());
				if (!dataModel.equals(existing)) {
					dataStructuresCoreService.updateAppend(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
					DATA_STRUCTURE_APPEND_MODELS.put(dataModel.getName(), dataModel);
					logger.info("Synchronized a modified Append Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
				}
			}
			APPEND_SYNCHRONIZED.add(dataModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize delete.
	 *
	 * @param dataModel
	 *            the data model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeDelete(DataStructureDataDeleteModel dataModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsDelete(dataModel.getLocation())) {
				dataStructuresCoreService.createDelete(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
				DATA_STRUCTURE_DELETE_MODELS.put(dataModel.getName(), dataModel);
				logger.info("Synchronized a new Delete Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
			} else {
				DataStructureDataDeleteModel existing = dataStructuresCoreService.getDelete(dataModel.getLocation());
				if (!dataModel.equals(existing)) {
					dataStructuresCoreService.updateDelete(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
					DATA_STRUCTURE_DELETE_MODELS.put(dataModel.getName(), dataModel);
					logger.info("Synchronized a modified Delete Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
				}
			}
			DELETE_SYNCHRONIZED.add(dataModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Synchronize update.
	 *
	 * @param dataModel
	 *            the data model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeUpdate(DataStructureDataUpdateModel dataModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsUpdate(dataModel.getLocation())) {
				dataStructuresCoreService.createUpdate(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
				DATA_STRUCTURE_UPDATE_MODELS.put(dataModel.getName(), dataModel);
				logger.info("Synchronized a new Update Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
			} else {
				DataStructureDataUpdateModel existing = dataStructuresCoreService.getUpdate(dataModel.getLocation());
				if (!dataModel.equals(existing)) {
					dataStructuresCoreService.updateUpdate(dataModel.getLocation(), dataModel.getName(), dataModel.getHash());
					DATA_STRUCTURE_UPDATE_MODELS.put(dataModel.getName(), dataModel);
					logger.info("Synchronized a modified Update Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
				}
			}
			UPDATE_SYNCHRONIZED.add(dataModel.getLocation());
		} catch (DataStructuresException e) {
			throw new SynchronizationException(e);
		}
	}
	
	/**
	 * Synchronize schema.
	 *
	 * @param schemaModel
	 *            the schema model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeSchema(DataStructureSchemaModel schemaModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsSchema(schemaModel.getLocation())) {
				dataStructuresCoreService.createSchema(schemaModel.getLocation(), schemaModel.getName(), schemaModel.getHash());
				DATA_STRUCTURE_SCHEMA_MODELS.put(schemaModel.getName(), schemaModel);
				addDataStructureModelsFromSchema(schemaModel);
				logger.info("Synchronized a new Schema file [{}] from location: {}", schemaModel.getName(), schemaModel.getLocation());
				applyArtefactState(schemaModel, SCHEMA_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				DataStructureSchemaModel existing = dataStructuresCoreService.getSchema(schemaModel.getLocation());
				if (!schemaModel.equals(existing)) {
					dataStructuresCoreService.updateSchema(schemaModel.getLocation(), schemaModel.getName(), schemaModel.getHash());
					DATA_STRUCTURE_SCHEMA_MODELS.put(schemaModel.getName(), schemaModel);
					addDataStructureModelsFromSchema(schemaModel);
					logger.info("Synchronized a modified Schema file [{}] from location: {}", schemaModel.getName(), schemaModel.getLocation());
					applyArtefactState(schemaModel, SCHEMA_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			SCHEMA_SYNCHRONIZED.add(schemaModel.getLocation());
		} catch (DataStructuresException e) {
			applyArtefactState(schemaModel, SCHEMA_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			logProblem(e.getMessage(), ERROR_TYPE, schemaModel.getLocation(), SCHEMA_ARTEFACT.getId());
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Adds the data structure models from schema.
	 *
	 * @param schemaModel the schema model
	 */
	private void addDataStructureModelsFromSchema(DataStructureSchemaModel schemaModel) {
		for (DataStructureTableModel tableModel : schemaModel.getTables()) {
			DATA_STRUCTURE_MODELS.put(tableModel.getName(), tableModel);
		}
		for (DataStructureViewModel viewModel : schemaModel.getViews()) {
			DATA_STRUCTURE_MODELS.put(viewModel.getName(), viewModel);
		}
	}

	/**
	 * Synchronize registry.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Data Structures from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Data Structures from Registry.");
	}

	/**
	 * Synchronize resource.
	 *
	 * @param resource the resource
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		String registryPath = getRegistryPath(resource);
		byte[] content = resource.getContent();
		String contentAsString;
		try {
			contentAsString = IOUtils.toString(new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new SynchronizationException(e);
		}
		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_TABLE)) {
			DataStructureTableModel tableModel = dataStructuresCoreService.parseTable(content);
			tableModel.setLocation(registryPath);
			synchronizeTable(tableModel);
			return;
		}

		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_VIEW)) {
			DataStructureViewModel viewModel = dataStructuresCoreService.parseView(content);
			viewModel.setLocation(registryPath);
			synchronizeView(viewModel);
			return;
		}

		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_REPLACE)) {
			DataStructureDataReplaceModel dataModel = dataStructuresCoreService.parseReplace(registryPath, contentAsString);
			dataModel.setLocation(registryPath);
			synchronizeReplace(dataModel);
			return;
		}

		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_APPEND)) {
			DataStructureDataAppendModel dataModel = dataStructuresCoreService.parseAppend(registryPath, contentAsString);
			dataModel.setLocation(registryPath);
			synchronizeAppend(dataModel);
			return;
		}

		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_DELETE)) {
			DataStructureDataDeleteModel dataModel = dataStructuresCoreService.parseDelete(registryPath, contentAsString);
			dataModel.setLocation(registryPath);
			synchronizeDelete(dataModel);
			return;
		}

		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_UPDATE)) {
			DataStructureDataUpdateModel dataModel = dataStructuresCoreService.parseUpdate(registryPath, contentAsString);
			dataModel.setLocation(registryPath);
			synchronizeUpdate(dataModel);
			return;
		}
		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_SCHEMA)) {
			DataStructureSchemaModel schemaModel = dataStructuresCoreService.parseSchema(registryPath, contentAsString);
			schemaModel.setLocation(registryPath);
			synchronizeSchema(schemaModel);
			return;
		}
	}

	/**
	 * Cleanup.
	 *
	 * @throws SynchronizationException the synchronization exception
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Data Structures...");
		super.cleanup();

		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				List<DataStructureTableModel> tableModels = dataStructuresCoreService.getTables();
				for (DataStructureTableModel tableModel : tableModels) {
					if (!TABLES_SYNCHRONIZED.contains(tableModel.getLocation())) {
						dataStructuresCoreService.removeTable(tableModel.getLocation());
						executeTableDrop(connection, tableModel);
						logger.warn("Cleaned up Table [{}] from location: {}", tableModel.getName(), tableModel.getLocation());
					}
				}

				List<DataStructureViewModel> viewModels = dataStructuresCoreService.getViews();
				for (DataStructureViewModel viewModel : viewModels) {
					if (!VIEWS_SYNCHRONIZED.contains(viewModel.getLocation())) {
						dataStructuresCoreService.removeView(viewModel.getLocation());
						executeViewDrop(connection, viewModel);
						logger.warn("Cleaned up View [{}] from location: {}", viewModel.getName(), viewModel.getLocation());
					}
				}

				List<DataStructureDataReplaceModel> dataReplaceModels = dataStructuresCoreService.getReplaces();
				for (DataStructureDataReplaceModel dataModel : dataReplaceModels) {
					if (!REPLACE_SYNCHRONIZED.contains(dataModel.getLocation())) {
						dataStructuresCoreService.removeReplace(dataModel.getLocation());
						logger.warn("Cleaned up Replace Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
					}
				}

				List<DataStructureDataAppendModel> dataAppendModels = dataStructuresCoreService.getAppends();
				for (DataStructureDataAppendModel dataModel : dataAppendModels) {
					if (!APPEND_SYNCHRONIZED.contains(dataModel.getLocation())) {
						dataStructuresCoreService.removeAppend(dataModel.getLocation());
						logger.warn("Cleaned up Append Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
					}
				}

				List<DataStructureDataDeleteModel> dataDeleteModels = dataStructuresCoreService.getDeletes();
				for (DataStructureDataDeleteModel dataModel : dataDeleteModels) {
					if (!DELETE_SYNCHRONIZED.contains(dataModel.getLocation())) {
						dataStructuresCoreService.removeDelete(dataModel.getLocation());
						logger.warn("Cleaned up Delete Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
					}
				}

				List<DataStructureDataUpdateModel> dataUpdateModels = dataStructuresCoreService.getUpdates();
				for (DataStructureDataUpdateModel dataModel : dataUpdateModels) {
					if (!UPDATE_SYNCHRONIZED.contains(dataModel.getLocation())) {
						dataStructuresCoreService.removeUpdate(dataModel.getLocation());
						logger.warn("Cleaned up Update Data file [{}] from location: {}", dataModel.getName(), dataModel.getLocation());
					}
				}
				
				List<DataStructureSchemaModel> schemaModels = dataStructuresCoreService.getSchemas();
				for (DataStructureSchemaModel schemaModel : schemaModels) {
					if (!SCHEMA_SYNCHRONIZED.contains(schemaModel.getLocation())) {
						dataStructuresCoreService.removeSchema(schemaModel.getLocation());
						logger.warn("Cleaned up Schema Data file [{}] from location: {}", schemaModel.getName(), schemaModel.getLocation());
					}
				}
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (DataStructuresException | SQLException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Data Structures.");
	}

	/**
	 * Update database schema.
	 */
	private void updateDatabaseSchema() {

		if (DATA_STRUCTURE_MODELS.isEmpty()) {
			logger.trace("No Data Structures to update.");
			return;
		}

		List<String> errors = new ArrayList<String>();
		try {
			Connection connection = null;
			try {
				connection = getDataSource().getConnection();
				
				TopologicalSorter<TopologyDataStructureModelWrapper> sorter = new TopologicalSorter<>();
				TopologicalDepleter<TopologyDataStructureModelWrapper> depleter = new TopologicalDepleter<>();
				
				List<TopologyDataStructureModelWrapper> list = new ArrayList<TopologyDataStructureModelWrapper>();
				Map<String, TopologyDataStructureModelWrapper> wrappers = new HashMap<String, TopologyDataStructureModelWrapper>();
				for (DataStructureModel model : DATA_STRUCTURE_MODELS.values()) {
					TopologyDataStructureModelWrapper wrapper = new TopologyDataStructureModelWrapper(this, connection, model, wrappers);
					list.add(wrapper);
				}
				
				// Topological sorting by dependencies
				list = sorter.sort(list);
				
				// Reverse the order
				Collections.reverse(list);
				
				// drop views in a reverse order
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_VIEW_DROP.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_VIEW_DROP.toString(), VIEW_ARTEFACT, ArtefactState.FAILED_DELETE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
				// drop tables' foreign keys in a reverse order
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_TABLE_FOREIGN_KEYS_DROP.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_TABLE_FOREIGN_KEYS_DROP.toString(), TABLE_ARTEFACT, ArtefactState.FAILED_DELETE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
				// drop tables in a reverse order
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_TABLE_DROP.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_TABLE_DROP.toString(), TABLE_ARTEFACT, ArtefactState.FAILED_DELETE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
				// Return back to the sorted the order 
				Collections.reverse(list);
				
				// process tables
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_TABLE_CREATE.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_TABLE_CREATE.toString(), TABLE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
				// process tables foreign keys
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_TABLE_FOREIGN_KEYS_CREATE.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_TABLE_FOREIGN_KEYS_CREATE.toString(), TABLE_ARTEFACT, ArtefactState.FAILED_CREATE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
				// process tables foreign keys
				try {
					List<TopologyDataStructureModelWrapper> results = depleter.deplete(list, TopologyDataStructureModelEnum.EXECUTE_VIEW_CREATE.toString());
					printErrors(errors, results, TopologyDataStructureModelEnum.EXECUTE_VIEW_CREATE.toString(), VIEW_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					errors.add(e.getMessage());
				}
				
			} finally {
				if (connection != null) {
					connection.close();
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			errors.add(e.getMessage());
		} finally {
			logger.error(concatenateListOfStrings(errors, "\n---\n"));
		}
	}

	/**
	 * Prints the errors.
	 *
	 * @param errors the errors
	 * @param results the results
	 * @param flow the flow
	 * @param artefact the artefact
	 * @param state the state
	 */
	private void printErrors(List<String> errors, List<TopologyDataStructureModelWrapper> results, String flow, ISynchronizerArtefactType artefact, ISynchronizerArtefactType.ArtefactState state) {
		if (results.size() > 0) {
			for (TopologyDataStructureModelWrapper result : results) {
				String errorMessage = String.format("Undepleted: %s in operation: %s", result.getId(), flow);
				logger.error(errorMessage);
				errors.add(errorMessage);
				applyArtefactState(result.getModel(), artefact, state, errorMessage);
			}
		}
	}



	/**
	 * Concatenate list of strings.
	 *
	 * @param list
	 *            the list
	 * @param separator
	 *            the separator
	 * @return the string
	 */
	private static String concatenateListOfStrings(List<String> list, String separator) {
		StringBuffer buff = new StringBuffer();
		for (String s : list) {
			buff.append(s).append(separator);
		}
		return buff.toString();
	}

	// Content

	/** The Constant COLUMN_NAME. */
	private static final String COLUMN_NAME = "COLUMN_NAME";

	/**
	 * Update database content.
	 */
	private void updateDatabaseContent() {

		// Replace
		for (String dsName : DATA_STRUCTURE_REPLACE_MODELS.keySet()) {
			DataStructureDataReplaceModel model = DATA_STRUCTURE_REPLACE_MODELS.get(dsName);
			try {
				executeReplaceUpdate(model);
				applyArtefactState(model, REPLACE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE_UPDATE);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				applyArtefactState(model, REPLACE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			}
		}

		// Append
		for (String dsName : DATA_STRUCTURE_APPEND_MODELS.keySet()) {
			DataStructureDataAppendModel model = DATA_STRUCTURE_APPEND_MODELS.get(dsName);
			try {
				executeAppendUpdate(model);
				applyArtefactState(model, APPEND_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE_UPDATE);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				applyArtefactState(model, APPEND_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			}
		}

		// Delete
		for (String dsName : DATA_STRUCTURE_DELETE_MODELS.keySet()) {
			DataStructureDataDeleteModel model = DATA_STRUCTURE_DELETE_MODELS.get(dsName);
			try {
				executeDeleteUpdate(model);
				applyArtefactState(model, DELETE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE_UPDATE);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				applyArtefactState(model, DELETE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			}
		}

		// Update
		for (String dsName : DATA_STRUCTURE_UPDATE_MODELS.keySet()) {
			DataStructureDataUpdateModel model = DATA_STRUCTURE_UPDATE_MODELS.get(dsName);
			try {
				executeUpdateUpdate(model);
				applyArtefactState(model, UPDATE_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE_UPDATE);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				applyArtefactState(model, UPDATE_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			}
		}

	}

	/**
	 * Process the data rows in the 'replace' mode.
	 *
	 * @param model            the model
	 * @throws Exception             in case of database error
	 */
	public void executeReplaceUpdate(DataStructureDataReplaceModel model) throws Exception {
		logger.info("Processing rows in mode 'replace': " + model.getLocation());

		byte[] content = model.getContent().getBytes();

		if (content.length != 0) {
			String tableName = model.getName();
			deleteAllDataFromTable(tableName);

			TableImporter tableDataInserter = new TableImporter(getDataSource(), content, tableName);
			tableDataInserter.insert();
			moveSequence(tableName); // move the sequence just in case
		} else {
			throw new SynchronizationException("No replace content found in: " + model.getLocation());
		}
	}

	/**
	 * Process the data rows in the 'append' mode.
	 *
	 * @param model            the model
	 * @throws Exception             in case of database error
	 */
	public void executeAppendUpdate(DataStructureDataAppendModel model) throws Exception {
		logger.info("Processing rows in mode 'append': " + model.getLocation());
		String tableName = model.getName();
		int tableRowsCount = getTableRowsCount(tableName);
		if (tableRowsCount == 0) {
			byte[] content = model.getContent().getBytes();
			if (content.length != 0) {
				TableImporter tableDataInserter = new TableImporter(getDataSource(), content, tableName);
				tableDataInserter.insert();
				moveSequence(tableName); // move the sequence, to be able to add more records after the initial import
			} else {
				throw new SynchronizationException("No append content found in: " + model.getLocation());
			}
		} else {
			throw new SynchronizationException("Cannot append table data, as records already exists in table " + tableName);
		}
	}

	/**
	 * Process the data rows in the 'delete' mode.
	 *
	 * @param model            the model
	 * @throws Exception             in case of database error
	 */
	public void executeDeleteUpdate(DataStructureDataDeleteModel model) throws Exception {
		logger.info("Processing rows in mode 'delete': " + model.getLocation());
		String tableName = model.getName();
		String primaryKey = getPrimaryKey(tableName);
		byte[] content = model.getContent().getBytes();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8));
		String firstLine = reader.readLine();
		if ((firstLine != null) && firstLine.trim().equals("*")) {
			deleteAllDataFromTable(tableName);
		} else {
			deleteRowsDataFromTable(tableName, primaryKey, content);
		}
	}

	/**
	 * Process the data rows in the 'update' mode.
	 *
	 * @param model            the model
	 * @throws Exception             in case of database error
	 */
	public void executeUpdateUpdate(DataStructureDataUpdateModel model) throws Exception {
		logger.info("Processing rows in mode 'update': " + model.getLocation());
		String tableName = model.getName();
		String primaryKey = getPrimaryKey(tableName);
		byte[] content = model.getContent().getBytes();
		updateRowsDataInTable(DataStructuresUtils.getCaseSensitiveTableName(tableName), primaryKey, content);
	}

	/**
	 * Delete all data from table.
	 *
	 * @param tableName the table name
	 * @throws Exception the exception
	 */
	private void deleteAllDataFromTable(String tableName) throws Exception {
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			String sql = SqlFactory.getNative(connection).delete().from(DataStructuresUtils.getCaseSensitiveTableName(tableName)).build();
			PreparedStatement deleteStatement = connection.prepareStatement(sql);
			deleteStatement.execute();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Gets the table rows count.
	 *
	 * @param tableName the table name
	 * @return the table rows count
	 * @throws Exception the exception
	 */
	private int getTableRowsCount(String tableName) throws Exception {
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			String sql = SqlFactory.getNative(connection).select().column("COUNT(*)").from(tableName).build();
			PreparedStatement countStatement = connection.prepareStatement(sql);
			ResultSet rs = countStatement.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				return count;
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return -1;
	}

	/**
	 * Gets the primary key.
	 *
	 * @param tableName the table name
	 * @return the primary key
	 * @throws Exception the exception
	 */
	private String getPrimaryKey(String tableName) throws Exception {
		String result = null;
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			ResultSet primaryKeys = TableMetadataHelper.getPrimaryKeys(connection, tableName);
			List<String> primaryKeysList = new ArrayList<String>();
			while (primaryKeys.next()) {
				String columnName = primaryKeys.getString(COLUMN_NAME);
				primaryKeysList.add(columnName);
			}
			if (primaryKeysList.size() == 0) {
				throw new Exception(String.format("Trying to manipulate data records for a table without a primary key: %s", tableName));
			}
			if (primaryKeysList.size() > 1) {
				throw new Exception(
						String.format("Trying to manipulate data records for a table with more than one columns in the primary key: %s", tableName));
			}
			result = primaryKeysList.get(0);
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
		return result;
	}

	/**
	 * Delete rows data from table.
	 *
	 * @param tableName the table name
	 * @param primaryKey the primary key
	 * @param fileContent the file content
	 * @throws Exception the exception
	 */
	private void deleteRowsDataFromTable(String tableName, String primaryKey, byte[] fileContent) throws Exception {
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			List<String[]> records = TableDataReader.readRecords(new ByteArrayInputStream(fileContent));
			for (String[] record : records) {
				if (record.length > 0) {
					String sql = SqlFactory.getNative(connection).delete().from(tableName).where(primaryKey + " = ?").build();
					PreparedStatement deleteStatement = connection.prepareStatement(sql);
					deleteStatement.setObject(1, record[0]);
					deleteStatement.execute();
				} else {
					logger.error(String.format("Skipping deletion of an empty data row for table: %s", tableName));
				}
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Update rows data in table.
	 *
	 * @param tableName the table name
	 * @param primaryKey the primary key
	 * @param fileContent the file content
	 * @throws Exception the exception
	 */
	private void updateRowsDataInTable(String tableName, String primaryKey, byte[] fileContent) throws Exception {
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			List<String[]> records = TableDataReader.readRecords(new ByteArrayInputStream(fileContent));
			for (String[] record : records) {
				if (record.length > 0) {
					String sql = SqlFactory.getNative(connection).select().column("*").from(tableName).where(primaryKey + " = ?").build();
					PreparedStatement stmt = connection.prepareStatement(sql);
					stmt.setObject(1, record[0]);
					ResultSet rs = stmt.executeQuery();
					if (!rs.next()) {
						StringBuffer buff = new StringBuffer();
						for (String value : record) {
							buff.append(value).append(TableExporter.DATA_DELIMETER);
						}
						buff.deleteCharAt(buff.length() - 1);
						buff.append("\n");
						TableImporter tableDataInserter = new TableImporter(getDataSource(), buff.toString().getBytes(), tableName);
						tableDataInserter.insert();
					}
				} else {
					logger.error(String.format("Skipping update of an empty data row for table: %s", tableName));
				}
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Move sequence.
	 *
	 * @param tableName the table name
	 * @throws Exception the exception
	 * @throws SQLException the SQL exception
	 */
	protected void moveSequence(String tableName) throws Exception, SQLException {

		int tableRowsCount;
		tableRowsCount = getTableRowsCount(tableName);

		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
			connection.setAutoCommit(false);

			PersistenceManager<Identity> persistenceManager = new PersistenceManager<Identity>();
			if (!persistenceManager.tableExists(connection, Identity.class)) {
				persistenceManager.tableCreate(connection, Identity.class);
			}
			Identity identity = persistenceManager.find(connection, Identity.class, tableName);
			if (identity == null) {
				identity = new Identity();
				identity.setTable(tableName);
				identity.setValue(++tableRowsCount);
				persistenceManager.insert(connection, identity);
				return;
			}
			try {
				try {
					identity = persistenceManager.lock(connection, Identity.class, tableName);
					identity.setValue(++tableRowsCount);
					persistenceManager.update(connection, identity);
				} finally {
					connection.commit();
				}
			} catch (SQLException e) {
				throw new PersistenceException(e);
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Gets the priority.
	 *
	 * @return the priority
	 */
	@Override
	public int getPriority() {
		return 200;
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
		logger.info("Processing Update Table: " + tableModel.getName());
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
	public void executeTableCreate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
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
	public void executeTableForeignKeysCreate(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		TableForeignKeysCreateProcessor.execute(connection, tableModel);
	}

	/**
	 * Execute table alter.
	 *
	 * @param connection            the connection
	 * @param tableModel            the table model
	 * @throws SQLException the SQL exception
	 */
	public void executeTableAlter(Connection connection, DataStructureTableModel tableModel) throws SQLException {
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
	public void executeTableDrop(Connection connection, DataStructureTableModel tableModel) throws SQLException {
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
	public void executeTableForeignKeysDrop(Connection connection, DataStructureTableModel tableModel) throws SQLException {
		TableForeignKeysDropProcessor.execute(connection, tableModel);
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
		ViewCreateProcessor.execute(connection, viewModel);
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
		ViewDropProcessor.execute(connection, viewModel);
	}
	
	/** The Constant ERROR_TYPE. */
	private static final String ERROR_TYPE = "DATABASE";
	
	/** The Constant MODULE. */
	private static final String MODULE = "dirigible-database-data-structures";
	
	/**
	 * Use to log problem from artifact processing.
	 *
	 * @param errorMessage the error message
	 * @param errorType the error type
	 * @param location the location
	 * @param artifactType the artifact type
	 */
	private static void logProblem(String errorMessage, String errorType, String location, String artifactType) {
		try {
			ProblemsFacade.save(location, errorType, "", "", errorMessage, "", artifactType, MODULE, DataStructuresSynchronizer.class.getName(), IProblemsConstants.PROGRAM_DEFAULT);
		} catch (ProblemsException e) {
			logger.error(e.getMessage(), e.getMessage());
		}
	}

}
