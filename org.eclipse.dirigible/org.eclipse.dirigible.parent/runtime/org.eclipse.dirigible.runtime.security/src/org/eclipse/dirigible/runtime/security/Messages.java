package org.eclipse.dirigible.runtime.security;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.runtime.security.messages"; //$NON-NLS-1$
	public static String SecuritySynchronizer_REFRESHING_OF_SECURED_LOCATIONS_FAILED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
