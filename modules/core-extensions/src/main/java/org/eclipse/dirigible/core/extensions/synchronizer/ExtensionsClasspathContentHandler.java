package org.eclipse.dirigible.core.extensions.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.extensions.api.IExtensionsCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(ExtensionsClasspathContentHandler.class);
	
	private ExtensionsSynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(ExtensionsSynchronizer.class);
	
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;
		
		try {
			if (path.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSIONPOINT)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredExtensionPoint(path);
			}
			
			if (path.endsWith(IExtensionsCoreService.FILE_EXTENSION_EXTENSION)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredExtension(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Extension Point or Extension is not valid", e);
		}
		
		return isValid;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}


}
