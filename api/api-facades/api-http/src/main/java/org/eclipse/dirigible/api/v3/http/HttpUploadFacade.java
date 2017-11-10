/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.http;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpUploadFacade.
 */
public class HttpUploadFacade implements IScriptingFacade {

	/** The Constant NO_VALID_REQUEST. */
	private static final String NO_VALID_REQUEST = "Trying to use HTTP Upload Facade without a valid Request";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpUploadFacade.class);

	/**
	 * Checks if is multipart content.
	 *
	 * @return true, if is multipart content
	 */
	public static final boolean isMultipartContent() {

		HttpServletRequest request = HttpRequestFacade.getRequest();
		if (request == null) {
			return false;
		}

		return ServletFileUpload.isMultipartContent(request);
	}

	/**
	 * Parses the request.
	 *
	 * @return the list
	 * @throws FileUploadException the file upload exception
	 */
	public static final List<FileItem> parseRequest() throws FileUploadException {
		ServletFileUpload servletFileUpload = new ServletFileUpload(new DiskFileItemFactory());
		HttpServletRequest request = HttpRequestFacade.getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<FileItem> fileItems = servletFileUpload.parseRequest(request);
		return fileItems;
	}

}
