package org.eclipse.swt.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.swt.widgets.messages"; //$NON-NLS-1$
	public static String FileDialog_ADD_FILE;
	public static String FileDialog_ALL_FILES;
	public static String FileDialog_SELECTED_FILENAME_FILTER;
	public static String FileDialog_SPECIFY_FILES_IN_ALL_EMPTY_SELECTORS_TO_CONTINUE;
	public static String FileDialog_TOTAL_UPLOAD_PROGRESS;
	public static String FileDialog_UPLOADING;
	public static String FileDialog_WAITING_FOR_UPLOADS_TO_FINISH;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
