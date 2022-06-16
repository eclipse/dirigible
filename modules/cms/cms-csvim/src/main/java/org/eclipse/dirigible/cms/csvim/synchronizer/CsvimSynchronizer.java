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
package org.eclipse.dirigible.cms.csvim.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.api.v3.security.UserFacade;
import org.eclipse.dirigible.cms.csvim.api.CsvimException;
import org.eclipse.dirigible.cms.csvim.api.ICsvimCoreService;
import org.eclipse.dirigible.cms.csvim.artefacts.CsvSynchronizationArtefactType;
import org.eclipse.dirigible.cms.csvim.artefacts.CsvimSynchronizationArtefactType;
import org.eclipse.dirigible.cms.csvim.definition.CsvDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvFileDefinition;
import org.eclipse.dirigible.cms.csvim.definition.CsvimDefinition;
import org.eclipse.dirigible.cms.csvim.service.CsvimCoreService;
import org.eclipse.dirigible.cms.csvim.service.CsvimDefinitionsTopologicalSorter;
import org.eclipse.dirigible.cms.csvim.service.CsvimProcessor;
import org.eclipse.dirigible.commons.config.StaticObjects;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.IOrderedSynchronizerContribution;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CSVIM Synchronizer.
 */
public class CsvimSynchronizer extends AbstractSynchronizer implements IOrderedSynchronizerContribution {

	private static final Logger logger = LoggerFactory.getLogger(CsvimSynchronizer.class);

	private static final Map<String, CsvimDefinition> CSVIM_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, CsvimDefinition>());

	private static final Map<String, CsvDefinition> CSV_PREDELIVERED = Collections
			.synchronizedMap(new HashMap<String, CsvDefinition>());

	private static final List<String> CSVIM_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private static final List<String> CSV_SYNCHRONIZED = Collections.synchronizedList(new ArrayList<String>());

	private CsvimCoreService csvimCoreService = new CsvimCoreService();

	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final CsvimSynchronizationArtefactType CSVIM_ARTEFACT = new CsvimSynchronizationArtefactType();

	private static final CsvSynchronizationArtefactType CSV_ARTEFACT = new CsvSynchronizationArtefactType();

	private DataSource dataSource = null;

	private static final Map<String, CsvimDefinition> CSVIM_MODELS = new LinkedHashMap<>();

	private CsvimProcessor csvimProcessor = new CsvimProcessor();
	
	protected synchronized DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = (DataSource) StaticObjects.get(StaticObjects.DATASOURCE);
		}
		return dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (CsvimSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing CSVIM files...");
				try {
					if (isSynchronizationEnabled()) {
						if (isSynchronizerSuccessful(
								"org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer")) {
							startSynchronization(SYNCHRONIZER_NAME);
							clearCache();
							synchronizePredelivered();
							synchronizeRegistry();
							processCsvimArtefacts();
							int immutableCsvimsCount = CSVIM_PREDELIVERED.size();
							int mutableCsvimsCount = CSVIM_SYNCHRONIZED.size();
							int immutableCsvsCount = CSV_PREDELIVERED.size();
							int mutableCsvsCount = CSV_SYNCHRONIZED.size();
							cleanup();
							clearCache();
							successfulSynchronization(SYNCHRONIZER_NAME, format(
									"Immutable CSVIM: {0}, Mutable CSVIM: {1}, Immutable CSV: {2}, Mutable CSV: {3}",
									immutableCsvimsCount, mutableCsvimsCount, immutableCsvsCount, mutableCsvsCount));
						} else {
							failedSynchronization(SYNCHRONIZER_NAME,
									"Skipped due to dependency: org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer");
						}
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for CSVIM failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for CSVIM files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing CSVIM files.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		CsvimSynchronizer synchronizer = new CsvimSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered extension point.
	 *
	 * @param csvimPath the extension point path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredCsvim(String csvimPath) throws IOException {
		InputStream in = CsvimSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + csvimPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			CsvimDefinition csvimDefinition = csvimCoreService.parseCsvim(json);
			csvimDefinition.setLocation(csvimPath);
			CSVIM_PREDELIVERED.put(csvimPath, csvimDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Register pre-delivered CSVIM.
	 *
	 * @param csvPath the csvim path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredCsv(String csvPath) throws IOException {
		InputStream in = CsvimSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + csvPath);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			CsvDefinition csvDefinition = new CsvDefinition();
			csvDefinition.setLocation(csvPath);
			csvDefinition.setHash(DigestUtils.md5Hex(json.getBytes()));
			CSV_PREDELIVERED.put(csvPath, csvDefinition);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	private void clearCache() {
		CSVIM_MODELS.clear();
		CSVIM_SYNCHRONIZED.clear();
		CSV_SYNCHRONIZED.clear();
	}

	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered CSVIM files...");
		// CSVIM
		for (CsvimDefinition csvimDefinition : CSVIM_PREDELIVERED.values()) {
			synchronizeCsvim(csvimDefinition);
		}
		// CSV
		for (CsvDefinition csvDefinition : CSV_PREDELIVERED.values()) {
			synchronizeCsv(csvDefinition);
		}
		logger.trace("Done synchronizing predelivered CSVIM files.");
	}

	private void synchronizeCsvim(CsvimDefinition csvimDefinition) throws SynchronizationException {
		try {
			if (!csvimCoreService.existsCsvim(csvimDefinition.getLocation())) {
				csvimCoreService.createCsvim(csvimDefinition.getLocation(), csvimDefinition.getHash());
				CSVIM_MODELS.put(csvimDefinition.getLocation(), csvimDefinition);
				logger.info("Synchronized a new CSVIM from location: {}", csvimDefinition.getLocation());
				applyArtefactState(csvimDefinition, CSVIM_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				CsvimDefinition existing = csvimCoreService.getCsvim(csvimDefinition.getLocation());
				if (!csvimDefinition.equals(existing)) {
					csvimCoreService.updateCsvim(csvimDefinition.getLocation(), csvimDefinition.getHash());
					logger.info("Synchronized a modified CSVIM file from location: {}", csvimDefinition.getLocation());
					CSVIM_MODELS.put(csvimDefinition.getLocation(), csvimDefinition);
					applyArtefactState(csvimDefinition, CSVIM_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			CSVIM_SYNCHRONIZED.add(csvimDefinition.getLocation());
		} catch (CsvimException e) {
			applyArtefactState(csvimDefinition, CSVIM_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	private void synchronizeCsv(CsvDefinition csvDefinition) throws SynchronizationException {
		try {
			if (!csvimCoreService.existsCsv(csvDefinition.getLocation())) {
				csvimCoreService.createCsv(csvDefinition.getLocation(), csvDefinition.getHash());
				logger.info("Synchronized a new CSV from location: {}", csvDefinition.getLocation());
				applyArtefactState(csvDefinition, CSV_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
			} else {
				CsvDefinition existing = csvimCoreService.getCsv(csvDefinition.getLocation());
				if (!csvDefinition.equals(existing)) {
					csvimCoreService.updateCsv(csvDefinition.getLocation(), csvDefinition.getHash(), false);
					logger.info("Synchronized a modified CSV file from location: {}", csvDefinition.getLocation());
					applyArtefactState(csvDefinition, CSV_ARTEFACT, ArtefactState.SUCCESSFUL_UPDATE);
				}
			}
			CSV_SYNCHRONIZED.add(csvDefinition.getLocation());
		} catch (CsvimException e) {
			applyArtefactState(csvDefinition, CSV_ARTEFACT, ArtefactState.FAILED_CREATE_UPDATE, e.getMessage());
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#
	 * synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing CSVIM files from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing CSVIM files from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#
	 * synchronizeResource(org.eclipse.dirigible. repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(ICsvimCoreService.FILE_EXTENSION_CSVIM)) {
			CsvimDefinition csvimDefinition = csvimCoreService.parseCsvim(resource.getContent());
			csvimDefinition.setLocation(getRegistryPath(resource));
			csvimDefinition.setHash(DigestUtils.md5Hex(resource.getContent()));
			csvimDefinition.setCreatedBy(UserFacade.getName());
			csvimDefinition.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			synchronizeCsvim(csvimDefinition);
		}
		if (resourceName.endsWith(ICsvimCoreService.FILE_EXTENSION_CSV)) {
			CsvDefinition csvDefinition = new CsvDefinition();
			csvDefinition.setLocation(getRegistryPath(resource));
			csvDefinition.setHash(DigestUtils.md5Hex(resource.getContent()));
			csvDefinition.setImported(false);
			csvDefinition.setCreatedBy(UserFacade.getName());
			csvDefinition.setCreatedAt(new Timestamp(System.currentTimeMillis()));

			synchronizeCsv(csvDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up CSVIM files...");
		super.cleanup();

		try {
			List<CsvimDefinition> csvimDefinitions = csvimCoreService.getCsvims();
			for (CsvimDefinition csvimDefinition : csvimDefinitions) {
				if (!CSVIM_SYNCHRONIZED.contains(csvimDefinition.getLocation())) {
					csvimCoreService.removeCsvim(csvimDefinition.getLocation());
					logger.warn("Cleaned up CSVIM file from location: {}", csvimDefinition.getLocation());
				}
			}
			List<CsvDefinition> csvDefinitions = csvimCoreService.getCsvs();
			for (CsvDefinition csvDefinition : csvDefinitions) {
				if (!CSV_SYNCHRONIZED.contains(csvDefinition.getLocation())) {
					csvimCoreService.removeCsv(csvDefinition.getLocation());
					logger.warn("Cleaned up CSV file from location: {}", csvDefinition.getLocation());
				}
			}

		} catch (CsvimException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up CSVIM files.");
	}

	private void processCsvimArtefacts() {
		try (Connection connection = getDataSource().getConnection()) {
			for (String csvimArtifactKey : CSVIM_MODELS.keySet()) {
				executeCsvim(CSVIM_MODELS.get(csvimArtifactKey), connection);
			}
		} catch (SQLException e) {
			logger.error("Error occurred while importing the data from CSVIM files", e);
		}
	}

	private void executeCsvim(CsvimDefinition csvimDefinition, Connection connection) {
		List<CsvFileDefinition> configurationDefinitions = csvimDefinition.getCsvFileDefinitions();

		List<CsvFileDefinition> sortedConfigurationDefinitions = new ArrayList<>();

		CsvimDefinitionsTopologicalSorter.sort(configurationDefinitions, sortedConfigurationDefinitions, connection);

		for (CsvFileDefinition csvFileDefinition : sortedConfigurationDefinitions) {
			try {
				IResource resource = csvimProcessor.getCsvResource(csvFileDefinition);
				String content = csvimProcessor.getCsvContent(resource);
				CsvDefinition csvDefinition = csvimCoreService.getCsv(csvFileDefinition.getFile());
				String hash = DigestUtils.md5Hex(content.getBytes());
				if (hash.equals(csvDefinition.getHash())
						&& csvDefinition.getImported()) {
					continue;
				}
				csvimProcessor.process(csvFileDefinition, content, connection);
				csvimCoreService.updateCsv(csvFileDefinition.getFile(), hash, true);
			} catch (SQLException | CsvimException | IOException e) {
				logger.error(
						String.format("An error occurred while trying to execute the data import: %s", e.getMessage()),
						e);
			}
		}
	}

	@Override
	public int getPriority() {
		return 400;
	}
}
