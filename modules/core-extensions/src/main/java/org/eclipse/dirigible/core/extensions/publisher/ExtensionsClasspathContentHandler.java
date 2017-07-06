package org.eclipse.dirigible.core.extensions.publisher;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.extensions.api.IExtensionsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsClasspathContentHandler extends AbstractClasspathContentHandler implements IExtensionsConstants {

	private static final Logger logger = LoggerFactory.getLogger(ExtensionsClasspathContentHandler.class);
	
	private ExtensionsPublisher extensionsPublisher = StaticInjector.getInjector().getInstance(ExtensionsPublisher.class);
	
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;
		
		try {
			if (path.endsWith(FILE_EXTENSION_EXTENSIONPOINT)) {
				isValid = true;
				extensionsPublisher.registerPredeliveredExtensionPoint(path);
			}
			
			if (path.endsWith(FILE_EXTENSION_EXTENSION)) {
				isValid = true;
				extensionsPublisher.registerPredeliveredExtension(path);
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
