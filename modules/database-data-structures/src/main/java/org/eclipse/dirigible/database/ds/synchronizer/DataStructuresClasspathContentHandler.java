package org.eclipse.dirigible.database.ds.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.database.ds.api.IDataStructuresCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataStructuresClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(DataStructuresClasspathContentHandler.class);

	private DataStructuresSynchronizer dataStructuresSynchronizer = StaticInjector.getInjector().getInstance(DataStructuresSynchronizer.class);

	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IDataStructuresCoreService.FILE_EXTENSION_TABLE)) {
				isValid = true;
				dataStructuresSynchronizer.registerPredeliveredTable(path);
			}

			if (path.endsWith(IDataStructuresCoreService.FILE_EXTENSION_VIEW)) {
				isValid = true;
				dataStructuresSynchronizer.registerPredeliveredView(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Table or View is not valid", e);
		}

		return isValid;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
