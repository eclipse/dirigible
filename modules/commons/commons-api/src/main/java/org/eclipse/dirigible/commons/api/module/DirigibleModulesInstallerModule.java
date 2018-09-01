/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.commons.api.module;

import static java.text.MessageFormat.format;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

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
		for (AbstractDirigibleModule next : dirigibleModules) {
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
