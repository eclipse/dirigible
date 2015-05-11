package org.eclipse.rap.rwt.supplemental.fileupload.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.rap.rwt.supplemental.fileupload.internal.messages"; //$NON-NLS-1$
	public static String FileUploadProcessor_FILE_EXCEEDS_MAXIMUM_ALLOWED_SIZE;
	public static String FileUploadProcessor_NO_FILE_UPLOAD_DATA_FOUND_IN_REQUEST;
	public static String FileUploadServiceHandler_CONTENT_MUST_BE_IN_MULTIPART_TYPE;
	public static String FileUploadServiceHandler_INVALID_OR_MISSING_TOKEN;
	public static String FileUploadServiceHandler_ONLY_POST_REQUESTS_ALLOWED;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
