package org.eclipse.dirigible.repository.ext.utils;

import org.eclipse.dirigible.repository.api.ICommonConstants;

public class OSGiUtils {

	public static boolean isOSGiEnvironment() {
		return Boolean.parseBoolean(System.getProperty(ICommonConstants.INIT_PARAM_RUN_ON_OSGI));
	}

}
