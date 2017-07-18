package org.eclipse.dirigible.core.security.synchronizer;

import java.io.IOException;

import org.eclipse.dirigible.commons.api.content.AbstractClasspathContentHandler;
import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.core.security.api.ISecurityCoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecurityClasspathContentHandler extends AbstractClasspathContentHandler {

	private static final Logger logger = LoggerFactory.getLogger(SecurityClasspathContentHandler.class);
	
	private SecuritySynchronizer extensionsSynchronizer = StaticInjector.getInjector().getInstance(SecuritySynchronizer.class);
	
	@Override
	protected boolean isValid(String path) {
		boolean isValid = false;
		
		try {
			if (path.endsWith(ISecurityCoreService.FILE_EXTENSION_ACCESS)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredAccess(path);
			}
			if (path.endsWith(ISecurityCoreService.FILE_EXTENSION_ROLES)) {
				isValid = true;
				extensionsSynchronizer.registerPredeliveredRoles(path);
			}
		} catch (IOException e) {
			logger.error("Predelivered Security Access or Roles artifact is not valid", e);
		}
		
		return isValid;
	}
	
	@Override
	protected Logger getLogger() {
		return logger;
	}


}
