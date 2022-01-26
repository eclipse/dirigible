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
package org.eclipse.dirigible.core.test;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test supporting class, enabling dependency injection.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDirigibleTest {

	/**
	 * Dependency injection before execution of every test method.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Before
	public void beforeEveryMethod() throws IOException {
		Configuration.setSystemProperty("DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT", "./target/derby_test_database");
		DirigibleModulesInstallerModule.configure();
	}


}
