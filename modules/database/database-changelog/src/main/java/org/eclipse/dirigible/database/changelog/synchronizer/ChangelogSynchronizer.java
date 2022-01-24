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
package org.eclipse.dirigible.database.changelog.synchronizer;

import static java.text.MessageFormat.format;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.database.changelog.artefacts.ChangelogSynchronizationArtefactType;
import org.eclipse.dirigible.database.ds.api.DataStructuresException;
import org.eclipse.dirigible.database.ds.model.DataStructureChangelogModel;
import org.eclipse.dirigible.database.ds.model.IDataStructureModel;
import org.eclipse.dirigible.database.ds.service.DataStructuresCoreService;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;

/**
 * The Changelogs Synchronizer.
 */
public class ChangelogSynchronizer extends AbstractSynchronizer {

	private static final Logger logger = LoggerFactory.getLogger(ChangelogSynchronizer.class);

	private static final Map<String, DataStructureChangelogModel> CHANGELOG_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, DataStructureChangelogModel>());

	private static final List<String> CHANGELOG_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());
	
	private static final Map<String, DataStructureChangelogModel> DATA_STRUCTURE_CHANGELOG_MODELS = new LinkedHashMap<String, DataStructureChangelogModel>();

	private DataStructuresCoreService dataStructuresCoreService = new DataStructuresCoreService();
	
	private DataSource dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final ChangelogSynchronizationArtefactType CHANGELOG_ARTEFACT = new ChangelogSynchronizationArtefactType();
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (ChangelogSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Changelogs...");
				try {
					if (isSynchronizationEnabled()) {
						startSynchronization(SYNCHRONIZER_NAME);
						clearCache();
						synchronizePredelivered();
						synchronizeRegistry();
						updateChangelogs();
						int immutableSchemaCount = CHANGELOG_PREDELIVERED.size();
						
						int mutableSchemaCount = DATA_STRUCTURE_CHANGELOG_MODELS.size();
						
						cleanup(); // TODO drop tables and views for non-existing models
						clearCache();
						
						successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: [ Changelog: {1}], "
								+ "Mutable: [Changelog: {9}]", 
								immutableSchemaCount, mutableSchemaCount));
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Changelogs failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Changelogs files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Changelogs.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		ChangelogSynchronizer synchronizer = new ChangelogSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
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
		DataStructureChangelogModel model = dataStructuresCoreService.parseChangelog(contentPath, data);
		CHANGELOG_PREDELIVERED.put(contentPath, model);
	}

	private String loadResourceContent(String modelPath) throws IOException {
		InputStream in = ChangelogSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + modelPath);
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
		DATA_STRUCTURE_CHANGELOG_MODELS.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Changelogs...");
		
		// Schema
		for (DataStructureChangelogModel schema : CHANGELOG_PREDELIVERED.values()) {
			try {
				synchronizeSchema(schema);
			} catch (Exception e) {
				logger.error(format("Update schema [{0}] skipped due to an error: {1}", schema, e.getMessage()), e);
			}
		}
		
		logger.trace("Done synchronizing predelivered Changelogs.");
	}
	
	/**
	 * Synchronize schema.
	 *
	 * @param schemaModel
	 *            the schema model
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeSchema(DataStructureChangelogModel schemaModel) throws SynchronizationException {
		try {
			if (!dataStructuresCoreService.existsSchema(schemaModel.getLocation())) {
				dataStructuresCoreService.createSchema(schemaModel.getLocation(), schemaModel.getName(), schemaModel.getHash());
				DATA_STRUCTURE_CHANGELOG_MODELS.put(schemaModel.getName(), schemaModel);
				logger.info("Synchronized a new Schema file [{}] from location: {}", schemaModel.getName(), schemaModel.getLocation());
				applyArtefactState(schemaModel, CHANGELOG_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				DataStructureChangelogModel existing = dataStructuresCoreService.getChangelog(schemaModel.getLocation());
				if (!schemaModel.equals(existing)) {
					dataStructuresCoreService.updateSchema(schemaModel.getLocation(), schemaModel.getName(), schemaModel.getHash());
					DATA_STRUCTURE_CHANGELOG_MODELS.put(schemaModel.getName(), schemaModel);
					logger.info("Synchronized a modified Schema file [{}] from location: {}", schemaModel.getName(), schemaModel.getLocation());
					applyArtefactState(schemaModel, CHANGELOG_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			CHANGELOG_SYNCHRONIZED.add(schemaModel.getLocation());
		} catch (DataStructuresException e) {
			applyArtefactState(schemaModel, CHANGELOG_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Changelogs from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Changelogs from Registry.");
	}

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
		
		if (resourceName.endsWith(IDataStructureModel.FILE_EXTENSION_CHANGELOG)) {
			DataStructureChangelogModel schemaModel = dataStructuresCoreService.parseChangelog(registryPath, contentAsString);
			schemaModel.setLocation(registryPath);
			synchronizeSchema(schemaModel);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Changelogs...");
		super.cleanup();

		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				
				List<DataStructureChangelogModel> schemaModels = dataStructuresCoreService.getChangelogs();
				for (DataStructureChangelogModel schemaModel : schemaModels) {
					if (!CHANGELOG_SYNCHRONIZED.contains(schemaModel.getLocation())) {
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

		logger.trace("Done cleaning up Changelogs.");
	}

	/**
	 * Update database schema.
	 */
	private void updateChangelogs() {

		if (DATA_STRUCTURE_CHANGELOG_MODELS.isEmpty()) {
			logger.trace("No Changelogs to update.");
			return;
		}

		List<String> errors = new ArrayList<String>();
		try {
			Connection connection = null;
			try {
				connection = dataSource.getConnection();
				
				List<String> changelogs = new ArrayList<String>((DATA_STRUCTURE_CHANGELOG_MODELS.keySet()));
				
				for (String changelog : changelogs) {
					DataStructureChangelogModel model = DATA_STRUCTURE_CHANGELOG_MODELS.get(changelog);
					try {
						executeChangelogUpdate(connection, changelog, model);
						applyArtefactState(model, CHANGELOG_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE_UPDATE);
					} catch (URISyntaxException | LiquibaseException | IOException e) {
						logger.error(e.getMessage(), e);
						errors.add(e.getMessage());
						applyArtefactState(model, CHANGELOG_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
					}
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

	public void executeChangelogUpdate(Connection connection, String changelog,
			DataStructureChangelogModel model) throws DatabaseException, URISyntaxException, LiquibaseException, IOException {
		if (!changelog.endsWith(".json")) {
			changelog += ".json";
		}
		try (InputStream stream = new ByteArrayInputStream(model.getContent().getBytes(StandardCharsets.UTF_8))) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
			ChangelogResourceAccessor resourceAccessor = new ChangelogResourceAccessor(new URI(changelog), stream);
			Liquibase liquibase = new Liquibase(changelog, resourceAccessor, database);
		    try {
		        Contexts context = new Contexts();
		        liquibase.update(context);
		    } finally {
//				liquibase.close();
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

}
