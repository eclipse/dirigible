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
package org.eclipse.dirigible.core.migrations.synchronizer;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.eclipse.dirigible.core.migrations.api.MigrationsException;
import org.eclipse.dirigible.core.migrations.artefacts.MigrationSynchronizationArtefactType;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.definition.MigrationStatusDefinition;
import org.eclipse.dirigible.core.migrations.service.MigrationsCoreService;
import org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer;
import org.eclipse.dirigible.core.scheduler.api.IOrderedSynchronizerContribution;
import org.eclipse.dirigible.core.scheduler.api.ISynchronizerArtefactType.ArtefactState;
import org.eclipse.dirigible.core.scheduler.api.SchedulerException;
import org.eclipse.dirigible.core.scheduler.api.SynchronizationException;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;
import org.eclipse.dirigible.repository.api.IResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Migrations Synchronizer.
 */
public class MigrationsSynchronizer extends AbstractSynchronizer implements IOrderedSynchronizerContribution {

	private static final Logger logger = LoggerFactory.getLogger(MigrationsSynchronizer.class);

	private static final Map<String, MigrationDefinition> MIGRATIONS_PREDELIVERED = Collections.synchronizedMap(new HashMap<String, MigrationDefinition>());

	private static final Set<String> MIGRATIONS_SYNCHRONIZED = Collections.synchronizedSet(new HashSet<String>());

	private MigrationsCoreService migrationsCoreService = new MigrationsCoreService();
	
	private final String SYNCHRONIZER_NAME = this.getClass().getCanonicalName();

	private static final MigrationSynchronizationArtefactType MIGRATION_ARTEFACT = new MigrationSynchronizationArtefactType();

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.ISynchronizer#synchronize()
	 */
	@Override
	public void synchronize() {
		synchronized (MigrationsSynchronizer.class) {
			if (beforeSynchronizing()) {
				logger.trace("Synchronizing Migrations artifacts...");
				try {
					if (isSynchronizationEnabled()) {
						if (isSynchronizerSuccessful("org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer")) {
							startSynchronization(SYNCHRONIZER_NAME);
							clearCache();
							synchronizePredelivered();
							synchronizeRegistry();
							startMigrations();
							int immutableCount = MIGRATIONS_PREDELIVERED.size();
							int mutableCount = MIGRATIONS_SYNCHRONIZED.size();
							cleanup();
							clearCache();
							successfulSynchronization(SYNCHRONIZER_NAME, format("Immutable: {0}, Mutable: {1}", immutableCount, mutableCount));
						} else {
							failedSynchronization(SYNCHRONIZER_NAME, "Skipped due to dependency: org.eclipse.dirigible.database.ds.synchronizer.DataStructuresSynchronizer");
						}
					} else {
						logger.debug("Synchronization has been disabled");
					}
				} catch (Exception e) {
					logger.error("Synchronizing process for Migrations artifacts failed.", e);
					try {
						failedSynchronization(SYNCHRONIZER_NAME, e.getMessage());
					} catch (SchedulerException e1) {
						logger.error("Synchronizing process for Migrations files failed in registering the state log.", e);
					}
				}
				logger.trace("Done synchronizing Migrations artifacts.");
				afterSynchronizing();
			}
		}
	}

	/**
	 * Force synchronization.
	 */
	public static final void forceSynchronization() {
		MigrationsSynchronizer synchronizer = new MigrationsSynchronizer();
		synchronizer.setForcedSynchronization(true);
		try {
			synchronizer.synchronize();
		} finally {
			synchronizer.setForcedSynchronization(false);
		}
	}

	/**
	 * Register pre-delivered roles.
	 *
	 * @param location
	 *            the roles path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void registerPredeliveredMigrations(String location) throws IOException {
		InputStream in = MigrationsSynchronizer.class.getResourceAsStream("/META-INF/dirigible" + location);
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			MigrationDefinition migrationDefinition = migrationsCoreService.parseMigration(json);
			migrationDefinition.setLocation(location);
			MIGRATIONS_PREDELIVERED.put(location, migrationDefinition);
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
		MIGRATIONS_SYNCHRONIZED.clear();
	}

	/**
	 * Synchronize predelivered.
	 *
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizePredelivered() throws SynchronizationException {
		logger.trace("Synchronizing predelivered Migrations artifacts...");

		// Migrations
		for (MigrationDefinition migrationDefinition : MIGRATIONS_PREDELIVERED.values()) {
			synchronizeMigration(migrationDefinition);
		}

		logger.trace("Done synchronizing predelivered Migrations artifacts.");
	}

	/**
	 * Synchronize role.
	 *
	 * @param migrationDefinition
	 *            the migration definition
	 * @throws SynchronizationException
	 *             the synchronization exception
	 */
	private void synchronizeMigration(MigrationDefinition migrationDefinition) throws SynchronizationException {
		try {
			if (!migrationsCoreService.existsMigration(migrationDefinition.getLocation())) {
				migrationsCoreService.createMigration(migrationDefinition.getLocation(), migrationDefinition.getProject(), 
						migrationDefinition.getMajor(), migrationDefinition.getMinor(), migrationDefinition.getMicro(),
						migrationDefinition.getHandler(), migrationDefinition.getEngine(), migrationDefinition.getDescription());
				logger.info("Synchronized a new Migration procedure from location: {}", migrationDefinition.getLocation());
			} else {
				MigrationDefinition existing = migrationsCoreService.getMigration(migrationDefinition.getLocation());
				if (!migrationDefinition.equals(existing)) {
					logger.error("Modified Migration procedure was met during synchronization!");
				}
			}
			MIGRATIONS_SYNCHRONIZED.add(migrationDefinition.getLocation());
		} catch (MigrationsException e) {
			throw new SynchronizationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeRegistry()
	 */
	@Override
	protected void synchronizeRegistry() throws SynchronizationException {
		logger.trace("Synchronizing Migrations from Registry...");

		super.synchronizeRegistry();

		logger.trace("Done synchronizing Migrations from Registry.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#synchronizeResource(org.eclipse.dirigible.
	 * repository.api.IResource)
	 */
	@Override
	protected void synchronizeResource(IResource resource) throws SynchronizationException {
		String resourceName = resource.getName();
		if (resourceName.endsWith(IMigrationsCoreService.FILE_EXTENSION_MIGRATE)) {
			MigrationDefinition migrationDefinition = migrationsCoreService.parseMigration(resource.getContent());
			migrationDefinition.setLocation(getRegistryPath(resource));
			synchronizeMigration(migrationDefinition);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.core.scheduler.api.AbstractSynchronizer#cleanup()
	 */
	@Override
	protected void cleanup() throws SynchronizationException {
		logger.trace("Cleaning up Roles and Access artifacts...");
		super.cleanup();

		try {
			List<MigrationDefinition> migrationDefinitions = migrationsCoreService.getMigrations();
			for (MigrationDefinition migrationDefinition : migrationDefinitions) {
				if (!MIGRATIONS_SYNCHRONIZED.contains(migrationDefinition.getLocation())) {
					migrationsCoreService.removeMigration(migrationDefinition.getLocation());
					logger.warn("Cleaned up Migration definition from location: {}", migrationDefinition.getLocation());
				}
			}

		} catch (MigrationsException e) {
			throw new SynchronizationException(e);
		}

		logger.trace("Done cleaning up Migrations artifacts.");
	}
	
	private void startMigrations() {
		logger.trace("Start running Migrations...");

		List<String> migratedProjects = new ArrayList<String>();
		for (String migrationLocation : MIGRATIONS_SYNCHRONIZED) {
			String project = "";
			MigrationDefinition lastMigration = null;
			MigrationDefinition currentMigration = null;
			try {
				project = "location: " + migrationLocation;
				MigrationDefinition migrationDefinition = migrationsCoreService.getMigration(migrationLocation);
				project = migrationDefinition.getProject();
				if (migratedProjects.contains(project)) {
					continue;
				}
				List<MigrationDefinition> migrationDefinitions = migrationsCoreService.getMigrationsPerProject(project);
				MigrationStatusDefinition migrationStatusDefinition = migrationsCoreService.getMigrationStatus(project);
				for (MigrationDefinition migration : migrationDefinitions) {
					currentMigration = migration;
					if (migrationStatusDefinition != null) {
						if (migration.getMajor() > migrationStatusDefinition.getMajor()) {
							performMigration(migration);
							applyArtefactState(migration, MIGRATION_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
						} else if (migration.getMajor() == migrationStatusDefinition.getMajor() 
								&& migration.getMinor() > migrationStatusDefinition.getMinor()) {
							performMigration(migration);
							applyArtefactState(migration, MIGRATION_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
						} else if (migration.getMajor() == migrationStatusDefinition.getMajor() 
								&& migration.getMinor() == migrationStatusDefinition.getMinor()
								&& migration.getMicro() > migrationStatusDefinition.getMicro()) {
							performMigration(migration);
							applyArtefactState(migration, MIGRATION_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
						} else {
							String errorMessage = MessageFormat.format("Migration for project {0} with version {1}.{2}.{3} has been skipped because the project status is with a higher version", 
									project, migration.getMajor(), migration.getMinor(), migration.getMicro());
							logger.trace(errorMessage);
							applyArtefactState(migration, MIGRATION_ARTEFACT, ArtefactState.FAILED_CREATE, errorMessage);
						}
					} else {
						performMigration(migration);
						applyArtefactState(migration, MIGRATION_ARTEFACT, ArtefactState.SUCCESSFUL_CREATE);
					}
					lastMigration = migration;
				}
				if (migrationStatusDefinition == null) {
					migrationsCoreService.createMigrationStatus(project, lastMigration.getMajor(), lastMigration.getMinor(), lastMigration.getMicro(), 
							lastMigration.getLocation());
				} else {
					if (migrationStatusDefinition.getMajor() != lastMigration.getMajor()
							|| migrationStatusDefinition.getMinor() != lastMigration.getMinor()
							|| migrationStatusDefinition.getMicro() != lastMigration.getMicro()) {
						migrationsCoreService.updateMigrationStatus(project, lastMigration.getMajor(), lastMigration.getMinor(), lastMigration.getMicro(), 
								lastMigration.getLocation());
					}
				}
				migratedProjects.add(project);
			} catch (MigrationsException | ScriptingException e) {
				logger.error("Migration procedure for project {} artifacts failed.", project);
				logger.error("Migration procedure error: ", e);
				applyArtefactState(currentMigration, MIGRATION_ARTEFACT, ArtefactState.FAILED_CREATE, e.getMessage());
			}
			
		}

		logger.trace("Done running Migrations.");
	}

	private void performMigration(MigrationDefinition migration) throws ScriptingException {
		ScriptEngineExecutorsManager.executeServiceModule(migration.getEngine(), migration.getHandler(), null);
	}

	@Override
	public int getPriority() {
		return 300;
	}
}
