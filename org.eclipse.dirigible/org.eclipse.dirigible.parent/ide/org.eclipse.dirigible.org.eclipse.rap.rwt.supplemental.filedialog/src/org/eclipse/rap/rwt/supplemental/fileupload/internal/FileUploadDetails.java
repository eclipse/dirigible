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

import org.eclipse.rap.rwt.supplemental.fileupload.IFileUploadDetails;

public final class FileUploadDetails implements IFileUploadDetails {

	private final String fileName;
	private final String contentType;
	private final long contentLength;

	public FileUploadDetails(String fileName, String contentType,
			long contentLength) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentLength = contentLength;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public long getContentLength() {
		return contentLength;
	}
}
