package org.eclipse.swt.internal.widgets;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.swt.internal.widgets.messages"; //$NON-NLS-1$
	public static String ProgressCollector_TOTAL_UPLOAD_PROGRESS;
	public static String UploadPanel_BROWSE;
	public static String UploadPanel_REMOVE_FILE;
	public static String UploadPanel_SELECT_A_FILE;
	public static String UploadPanel_SELECTED_FILE;
	public static String UploadPanel_UPLOAD_PROGRESS;
	public static String UploadPanel_UPLOAD_PROGRESS2;
	public static String UploadPanel_WARNING_SELECTED_FILE_DOES_NOT_MATCH_FILTER;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
