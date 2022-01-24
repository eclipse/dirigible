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
package org.eclipse.dirigible.core.migrations.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.eclipse.dirigible.core.migrations.api.MigrationsException;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.service.MigrationsCoreService;
import org.eclipse.dirigible.core.test.AbstractDirigibleTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class MigrationsCoreServiceTest.
 */
public class MigrationsCoreServiceTest extends AbstractDirigibleTest {
	
	/** The migrations core service. */
	private IMigrationsCoreService migrationsCoreService;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.migrationsCoreService = new MigrationsCoreService();
	}
	
	/**
	 * Creates the migration.
	 *
	 * @throws MigrationsException the access exception
	 */
	@Test
	public void createMigration() throws MigrationsException {
		migrationsCoreService.getMigrations().forEach(migration->{
				try {
					migrationsCoreService.removeMigration(migration.getLocation());
				} catch (MigrationsException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
		});
		migrationsCoreService.removeMigration("/abc/my.migrations");
		migrationsCoreService.createMigration("/abc/my.migrations", "test_migration1", 1, 2, 3, "handelr1", "javascript",  "Test");
		List<MigrationDefinition> list = migrationsCoreService.getMigrations();
		assertEquals(1, list.size());
		MigrationDefinition MigrationDefinition = list.get(0);
		assertEquals("test_migration1", MigrationDefinition.getProject());
		assertEquals("Test", MigrationDefinition.getDescription());
		migrationsCoreService.removeMigration("/abc/my.migrations");
	}
	
	/**
	 * Gets the migration.
	 *
	 * @return the migration
	 * @throws MigrationsException the access exception
	 */
	@Test
	public void getMigration() throws MigrationsException {
		migrationsCoreService.removeMigration("/abc/my.migrations");
		migrationsCoreService.createMigration("/abc/my.migrations", "test_migration1", 1, 2, 3, "handelr1", "javascript",  "Test");
		MigrationDefinition migrationDefinition = migrationsCoreService.getMigration("/abc/my.migrations");
		assertEquals("test_migration1", migrationDefinition.getProject());
		assertEquals("Test", migrationDefinition.getDescription());
		migrationsCoreService.removeMigration("/abc/my.migrations");
	}
	
	/**
	 * Update migration.
	 *
	 * @throws MigrationsException the access exception
	 */
	@Test
	public void updatetMigration() throws MigrationsException {
		migrationsCoreService.removeMigration("/abc/my.migrations");
		migrationsCoreService.createMigration("/abc/my.migrations", "test_migration1", 1, 2, 3, "handelr1", "javascript",  "Test");
		MigrationDefinition migrationDefinition = migrationsCoreService.getMigration("/abc/my.migrations");
		assertEquals("test_migration1", migrationDefinition.getProject());
		assertEquals("Test", migrationDefinition.getDescription());
		migrationsCoreService.updateMigration("/abc/my.migrations", "test_migration1", 1, 2, 3, "handelr1", "javascript",  "Test 2");
		migrationDefinition = migrationsCoreService.getMigration("/abc/my.migrations");
		assertEquals("test_migration1", migrationDefinition.getProject());
		assertEquals("Test 2", migrationDefinition.getDescription());
		migrationsCoreService.removeMigration("/abc/my.migrations");
	}
	
	/**
	 * Removes the migration.
	 *
	 * @throws MigrationsException the access exception
	 */
	@Test
	public void removeMigration() throws MigrationsException {
		migrationsCoreService.removeMigration("/abc/my.migrations");
		migrationsCoreService.createMigration("/abc/my.migrations", "test_migration1", 1, 2, 3, "handelr1", "javascript",  "Test");
		MigrationDefinition migrationDefinition = migrationsCoreService.getMigration("/abc/my.migrations");
		assertEquals("test_migration1", migrationDefinition.getProject());
		assertEquals("Test", migrationDefinition.getDescription());
		migrationsCoreService.removeMigration("/abc/my.migrations");
		migrationDefinition = migrationsCoreService.getMigration("/abc/my.migrations");
		assertNull(migrationDefinition);
	}

}
