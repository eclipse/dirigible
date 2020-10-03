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
package org.eclipse.dirigible.commons.api.module;

import static java.text.MessageFormat.format;

import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.Set;
import java.util.stream.Collectors;

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
		ServiceLoader<AbstractDirigibleModule> dirigibleModules = ServiceLoader.load(AbstractDirigibleModule.class);

		List<Provider<AbstractDirigibleModule>> sortedDirigibleModules = dirigibleModules.stream().sorted((provider1, provider2) -> {
			AbstractDirigibleModule module1 = provider1.get();
			AbstractDirigibleModule module2 = provider2.get();
			int priorityDiff = module1.getPriority() - module2.getPriority();
			if (priorityDiff == 0) {
				priorityDiff = module1.getName().compareTo(module2.getName());
			}
			return priorityDiff;
		}).collect(Collectors.toList());

		for (Provider<AbstractDirigibleModule> nextProvider : sortedDirigibleModules) {
			AbstractDirigibleModule next = nextProvider.get();
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
