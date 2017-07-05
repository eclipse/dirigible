package org.eclipse.dirigible.commons.api.module;


import static java.text.MessageFormat.format;

import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.logging.LoggingHelper;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class DirigibleModulesInstallerModule extends AbstractModule {
	
	private LoggingHelper loggingHelper;
	
	public DirigibleModulesInstallerModule() {
		this(new LoggingHelper(LoggerFactory.getLogger(DirigibleModulesInstallerModule.class)));
	}

	public DirigibleModulesInstallerModule(LoggingHelper loggingHelper) {
		this.loggingHelper = loggingHelper;
	}

	@Override
	protected void configure() {
		loggingHelper.beginSection("Initializing Dirigible Modules...");
		ServiceLoader<AbstractDirigibleModule> dirigibleModules = ServiceLoader.load(AbstractDirigibleModule.class);
		for (AbstractDirigibleModule next : dirigibleModules) {
			loggingHelper.beginGroup(format("Installing Dirigible Module [{0}] ...", next.getName()));
			try {
				next.setLoggingHelper(loggingHelper);
				install(next);
			} catch (Throwable e) {
				loggingHelper.error(format("Failed installing Dirigible Module [{0}].", next.getName()), e);
			}
			loggingHelper.endGroup(format("Done installing Dirigible Module [{0}].", next.getName()));
		}
		loggingHelper.endSection("Done initializing Dirigible Modules.");
	}

}
