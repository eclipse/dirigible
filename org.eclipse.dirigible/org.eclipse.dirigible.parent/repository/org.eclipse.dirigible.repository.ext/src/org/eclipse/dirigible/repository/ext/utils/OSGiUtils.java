package org.eclipse.dirigible.repository.ext.utils;

public class OSGiUtils {

	public static boolean isOSGiEnvironment() {
		try {
			Class.forName("org.osgi.framework.ServiceReference").newInstance();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
