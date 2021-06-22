/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.commons.api.module;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.dirigible.commons.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

/**
 * The DirigibleModulesInstallerModule is the entry point of the Guice integration.
 */
public class DirigibleModulesInstallerModule extends AbstractModule {

	private Logger logger = LoggerFactory.getLogger(DirigibleModulesInstallerModule.class);
	
	private static Set<String> modules = new HashSet<String>();

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		logger.debug("Initializing Dirigible Modules...");

		while (!Configuration.LOADED) {
			logger.info("Waiting for Dirigible Configuration to be initialized");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// Do nothing
			}
		}

		ServiceLoader<AbstractDirigibleModule> dirigibleModules = ServiceLoader.load(AbstractDirigibleModule.class);

		List<AbstractDirigibleModule> sortedDirigibleModules = new ArrayList<AbstractDirigibleModule>();
		Iterator<AbstractDirigibleModule> iterator = dirigibleModules.iterator();
		while(iterator.hasNext()) {
			sortedDirigibleModules.add(iterator.next());
		}
		Collections.sort(sortedDirigibleModules, new Comparator<AbstractDirigibleModule>() {

			@Override
			public int compare(AbstractDirigibleModule module1, AbstractDirigibleModule module2) {
				int priorityDiff = module1.getPriority() - module2.getPriority();
				if (priorityDiff == 0) {
					priorityDiff = module1.getName().compareTo(module2.getName());
				}
				return priorityDiff;
			}
		});

		for (AbstractDirigibleModule next: sortedDirigibleModules) {
			logger.debug(format("Installing Dirigible Module [{0}] ...", next.getName()));
			try {
				install(next);
			} catch (Throwable e) {
				logger.error(format("Failed installing Dirigible Module [{0}].", next.getName()), e);
			}
			logger.debug(format("Done installing Dirigible Module [{0}].", next.getName()));
			modules.add(next.getName());
		}
		logger.debug("Done initializing Dirigible Modules.");
	}
	
	public static Set<String> getModules() {
		return modules;
	}

}
