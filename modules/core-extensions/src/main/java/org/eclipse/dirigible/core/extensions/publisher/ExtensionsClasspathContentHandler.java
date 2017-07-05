package org.eclipse.dirigible.core.extensions.publisher;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.core.extensions.IExtensionsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsClasspathContentHandler extends AbstractClasspathContentHandler implements IExtensionsConstants {

	private static final Logger logger = LoggerFactory.getLogger(ExtensionsClasspathContentHandler.class);
	
	@Override
	protected boolean isValid(String path) {
		return path.endsWith(FILE_EXTENSION_EXTENSIONPOINT)
				|| path.endsWith(FILE_EXTENSION_EXTENSION);
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}


}
