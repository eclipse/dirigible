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

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

// TODO: Auto-generated Javadoc
/**
 * The Class DirigibleModulesInstallerModule.
 */
public class DirigibleModulesInstallerModule extends AbstractModule {
	
	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(DirigibleModulesInstallerModule.class);
	
	/* (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		logger.trace("Initializing Dirigible Modules...");
		ServiceLoader<AbstractDirigibleModule> dirigibleModules = ServiceLoader.load(AbstractDirigibleModule.class);
		for (AbstractDirigibleModule next : dirigibleModules) {
			logger.trace(format("Installing Dirigible Module [{0}] ...", next.getName()));
			try {
				install(next);
			} catch (Throwable e) {
				logger.error(format("Failed installing Dirigible Module [{0}].", next.getName()), e);
			}
			logger.trace(format("Done installing Dirigible Module [{0}].", next.getName()));
		}
		logger.trace("Done initializing Dirigible Modules.");
	}

}
