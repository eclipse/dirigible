package org.eclipse.dirigible.ide.db.viewer.views.format;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.ide.db.viewer.views.format.messages"; //$NON-NLS-1$
	public static String ResultSetStringWriter_EMPTY_RESULT_SET;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
