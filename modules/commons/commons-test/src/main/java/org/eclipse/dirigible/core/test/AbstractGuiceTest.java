/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.core.test;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.module.DirigibleModulesInstallerModule;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.config.Configuration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.UnitOfWork;

/**
 * Test supporting class, enabling dependency injection.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractGuiceTest {

	/** The unit of work mock. */
	protected UnitOfWork unitOfWorkMock;

	/**
	 * Dependency injection before execution of every test method.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Before
	public void beforeEveryMethod() throws IOException {
		StaticInjector.setInjector(getInjector());
	}

	/**
	 * Gets the injector.
	 *
	 * @return the injector
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected Injector getInjector() throws IOException {
		// TODO: doublecheck the logic for cleaning up the DB
		// FileUtils.forceDelete(new File("./target/derby_test_database"));
		Configuration.setSystemProperty("DIRIGIBLE_DATABASE_DERBY_ROOT_FOLDER_DEFAULT", "./target/derby_test_database");
		return Guice.createInjector(new DirigibleModulesInstallerModule(), new Module() {

			@Override
			public void configure(Binder binder) {
				bind(binder);
			}

		});
	}

	/**
	 * Bind.
	 *
	 * @param binder
	 *            the binder
	 */
	protected void bind(Binder binder) {
		setUpMocks();

		binder.bind(UnitOfWork.class).toInstance(unitOfWorkMock);
	}

	/**
	 * Sets the up mocks.
	 */
	protected void setUpMocks() {
		this.unitOfWorkMock = Mockito.mock(UnitOfWork.class);
	}

}
