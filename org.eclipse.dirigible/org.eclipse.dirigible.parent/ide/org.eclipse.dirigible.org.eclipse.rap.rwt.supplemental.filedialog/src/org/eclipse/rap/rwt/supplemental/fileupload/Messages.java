package org.eclipse.rap.rwt.supplemental.fileupload;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.rap.rwt.supplemental.fileupload.messages"; //$NON-NLS-1$
	public static String DiskFileUploadReceiver_UNABLE_TO_CREATE_TEMP_DIRECTORY;
	public static String FileUploadHandler_LISTENER_IS_NULL;
	public static String FileUploadHandler_RECEIVER_IS_NULL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
