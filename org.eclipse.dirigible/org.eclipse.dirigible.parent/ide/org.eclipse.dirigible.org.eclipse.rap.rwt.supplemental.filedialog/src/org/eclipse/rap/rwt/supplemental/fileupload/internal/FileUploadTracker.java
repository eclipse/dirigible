/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload.internal;

import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadEvent;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;

final class FileUploadTracker {

	private final FileUploadHandler handler;
	private String contentType;
	private String fileName;
	private long contentLength;
	private long bytesRead;
	private Exception exception;

	FileUploadTracker(FileUploadHandler handler) {
		this.handler = handler;
	}

	void setContentType(String contentType) {
		this.contentType = contentType;
	}

	void setFileName(String fileName) {
		this.fileName = fileName;
	}

	void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	void setException(Exception exception) {
		this.exception = exception;
	}

	void handleProgress() {
		new InternalFileUploadEvent(handler).dispatchAsProgress();
	}

	void handleFinished() {
		new InternalFileUploadEvent(handler).dispatchAsFinished();
	}

	void handleFailed() {
		new InternalFileUploadEvent(handler).dispatchAsFailed();
	}

	private final class InternalFileUploadEvent extends FileUploadEvent {

		private static final long serialVersionUID = 1L;

		private InternalFileUploadEvent(FileUploadHandler source) {
			super(source);
		}

		public String getContentType() {
			return contentType;
		}

		public long getContentLength() {
			return contentLength;
		}

		public long getBytesRead() {
			return bytesRead;
		}

		public String getFileName() {
			return fileName;
		}

		public Exception getException() {
			return exception;
		}

		void dispatchAsProgress() {
			super.dispatchProgress();
		}

		void dispatchAsFinished() {
			super.dispatchFinished();
		}

		void dispatchAsFailed() {
			super.dispatchFailed();
		}
	}
}
