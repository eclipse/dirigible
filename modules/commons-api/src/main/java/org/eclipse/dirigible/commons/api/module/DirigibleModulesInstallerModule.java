package org.eclipse.dirigible.commons.api.module;


import static java.text.MessageFormat.format;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class DirigibleModulesInstallerModule extends AbstractModule {
	
	private static final Logger logger = LoggerFactory.getLogger(DirigibleModulesInstallerModule.class);

	@Override
	protected void configure() {
		ServiceLoader<AbstractDirigibleModule> dirigibleModules = ServiceLoader.load(AbstractDirigibleModule.class);
		for (AbstractDirigibleModule next : dirigibleModules) {
			logger.info(format("Installing Dirigible Module [{0}] ...", next.getName()));
			try {
				install(next);
			} catch (Throwable e) {
				logger.error(format("Failed installing Dirigible Module [{0}].", next.getName()), e);
			}
			logger.info(format("Done installing Dirigible Module [{0}].", next.getName()));
		}
	}

}
