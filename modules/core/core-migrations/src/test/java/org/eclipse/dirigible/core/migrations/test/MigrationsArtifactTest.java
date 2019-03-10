/**
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.migrations.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.core.migrations.api.IMigrationsCoreService;
import org.eclipse.dirigible.core.migrations.definition.MigrationDefinition;
import org.eclipse.dirigible.core.migrations.service.MigrationsCoreService;
import org.eclipse.dirigible.core.test.AbstractGuiceTest;
import org.junit.Before;
import org.junit.Test;

/**
 * The Class RolesArtifactTest.
 */
public class MigrationsArtifactTest extends AbstractGuiceTest {
	
	/** The security core service. */
	@Inject
	private IMigrationsCoreService migrationsCoreService;
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.migrationsCoreService = getInjector().getInstance(MigrationsCoreService.class);
	}
	
	/**
	 * Serialize test.
	 */
	@Test
	public void serializeTest() {
		MigrationDefinition migration = new MigrationDefinition();
		migration.setLocation("/path1/path2/file1.migrate");
		migration.setProject("myproject1");
		migration.setMajor(1);
		migration.setMinor(2);
		migration.setMicro(3);
		migration.setHandler("/migrations/migration.js");
		migration.setEngine("javascript");
		migration.setDescription("Project1 Description");
		assertNotNull(migrationsCoreService.serializeMigration(migration));
	}

	/**
	 * Parses the test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void parseTest() throws IOException {
		InputStream in = MigrationsArtifactTest.class.getResourceAsStream("/migrations/test.migrate");
		try {
			String json = IOUtils.toString(in, StandardCharsets.UTF_8);
			MigrationDefinition migration = migrationsCoreService.parseMigration(json);
			assertEquals("myproject1", migration.getProject());
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	
}
