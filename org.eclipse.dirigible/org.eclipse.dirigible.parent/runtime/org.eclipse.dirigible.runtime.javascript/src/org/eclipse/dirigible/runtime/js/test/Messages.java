package org.eclipse.dirigible.runtime.js.test;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dirigible.runtime.js.test.messages"; //$NON-NLS-1$
	public static String TestExecutionUpdater_TEST_EXECUTION_RESULT_FOR_S_S;
	public static String TestExecutionUpdater_TEST_EXECUTION_S_COMPLETED;
	public static String TestExecutionUpdater_TEST_EXECUTION_STARTED_FOR_S;
	public static String TestExecutionUpdater_TESTS_EXECUTION_STARTED_FOR_D;
	public static String TestExecutionUpdater_TESTS_EXECUTION_COMPLETED;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
