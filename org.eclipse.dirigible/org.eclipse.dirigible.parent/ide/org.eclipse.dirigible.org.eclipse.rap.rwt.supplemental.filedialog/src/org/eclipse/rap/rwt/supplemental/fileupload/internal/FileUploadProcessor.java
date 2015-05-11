/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload.internal;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadReceiver;
import org.eclipse.rap.rwt.supplemental.fileupload.IFileUploadDetails;

final class FileUploadProcessor {

	private static final String FILE_EXCEEDS_MAXIMUM_ALLOWED_SIZE = Messages.FileUploadProcessor_FILE_EXCEEDS_MAXIMUM_ALLOWED_SIZE;
	private static final String NO_FILE_UPLOAD_DATA_FOUND_IN_REQUEST = Messages.FileUploadProcessor_NO_FILE_UPLOAD_DATA_FOUND_IN_REQUEST;
	private final FileUploadHandler handler;
	private final FileUploadTracker tracker;

	FileUploadProcessor(FileUploadHandler handler) {
		this.handler = handler;
		tracker = new FileUploadTracker(handler);
	}

	void handleFileUpload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			DiskFileItem fileItem = readUploadedFileItem(request);
			if (fileItem != null) {
				String fileName = stripFileName(fileItem.getName());
				String contentType = fileItem.getContentType();
				long contentLength = fileItem.getSize();
				tracker.setFileName(fileName);
				tracker.setContentType(contentType);
				FileUploadReceiver receiver = handler.getReceiver();
				IFileUploadDetails details = new FileUploadDetails(fileName,
						contentType, contentLength);
				receiver.receive(fileItem.getInputStream(), details);
				tracker.handleFinished();
			} else {
				String errorMessage = NO_FILE_UPLOAD_DATA_FOUND_IN_REQUEST;
				tracker.setException(new Exception(errorMessage));
				tracker.handleFailed();
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						errorMessage);
			}
		} catch (FileSizeLimitExceededException exception) {
			// Note: Apache fileupload 1.2 will throw an exception after the
			// upload is finished.
			// Therefore we handle it in the progress listener and ignore this
			// kind of exceptions here
			// https://issues.apache.org/jira/browse/FILEUPLOAD-145
			response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
					exception.getMessage());
		} catch (Exception exception) {
			tracker.setException(exception);
			tracker.handleFailed();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					exception.getMessage());
		}
	}

	private DiskFileItem readUploadedFileItem(HttpServletRequest request)
			throws FileUploadException {
		ServletFileUpload upload = createUpload();
		DiskFileItem result = null;
		List<?> uploadedItems = upload.parseRequest(request);
		// TODO [rst] Support multiple fields in multipart message
		if (uploadedItems.size() > 0) {
			// TODO [rst] Upload fails if the file is not the first field
			DiskFileItem fileItem = (DiskFileItem) uploadedItems.get(0);
			// Don't check for file size == 0 because this would prevent
			// uploading empty files
			if (!fileItem.isFormField()) {
				result = fileItem;
			}
		}
		return result;
	}

	private ServletFileUpload createUpload() {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setFileCleaningTracker(CleaningTrackerUtil
				.getCleaningTracker(true));
		ServletFileUpload result = new ServletFileUpload(factory);
		long maxFileSize = getMaxFileSize();
		result.setFileSizeMax(maxFileSize);
		ProgressListener listener = createProgressListener(maxFileSize);
		result.setProgressListener(listener);
		return result;
	}

	private ProgressListener createProgressListener(final long maxFileSize) {
		ProgressListener result = new ProgressListener() {
			long prevTotalBytesRead = -1;

			public void update(long totalBytesRead, long contentLength, int item) {
				// Depending on the servlet engine and other environmental
				// factors,
				// this listener may be notified for every network packet, so
				// don't notify unless there
				// is an actual increase.
				if (totalBytesRead > prevTotalBytesRead) {
					prevTotalBytesRead = totalBytesRead;
					// Note: Apache fileupload 1.2.x will throw an exception
					// after the upload is finished.
					// So we handle the file size violation as best we can from
					// here.
					// https://issues.apache.org/jira/browse/FILEUPLOAD-145
					if (maxFileSize != -1 && contentLength > maxFileSize) {
						tracker.setException(new Exception(
								FILE_EXCEEDS_MAXIMUM_ALLOWED_SIZE));
						tracker.handleFailed();
					} else {
						tracker.setContentLength(contentLength);
						tracker.setBytesRead(totalBytesRead);
						tracker.handleProgress();
					}
				}
			}
		};
		return result;
	}

	private long getMaxFileSize() {
		return handler.getMaxFileSize();
	}

	private static String stripFileName(String name) {
		String result = name;
		int lastSlash = result.lastIndexOf('/');
		if (lastSlash != -1) {
			result = result.substring(lastSlash + 1);
		} else {
			int lastBackslash = result.lastIndexOf('\\');
			if (lastBackslash != -1) {
				result = result.substring(lastBackslash + 1);
			}
		}
		return result;
	}

}
