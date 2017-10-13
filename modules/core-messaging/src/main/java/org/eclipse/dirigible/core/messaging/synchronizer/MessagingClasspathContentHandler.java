package org.eclipse.dirigible.core.messaging.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.messaging.api.IMessagingCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagingClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessagingClasspathContentHandler.class);

	private MessagingSynchronizer messagingSynchronizer = StaticInjector.getInjector().getInstance(MessagingSynchronizer.class);

	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;

		try {
			if (path.endsWith(IMessagingCoreService.FILE_EXTENSION_LISTENER)) {
				isValid = true;
				messagingSynchronizer.registerPredeliveredListener(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Listener is not valid", e);
		}

		return isValid;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

}
