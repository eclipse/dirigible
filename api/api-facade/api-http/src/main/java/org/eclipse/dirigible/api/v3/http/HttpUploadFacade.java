/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
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

/**
 * Java facade for working uploading files
 */
public class HttpUploadFacade implements IScriptingFacade {

	private static final String NO_VALID_REQUEST = "Trying to use HTTP Upload Facade without a valid Request";

	private static final Logger logger = LoggerFactory.getLogger(HttpUploadFacade.class);

	/**
	 * Checks if the request contains multipart content.
	 *
	 * @return true, if the request contains is multipart content
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
	 * @return A list of FileItem instances parsed from the request, in the order that they were transmitted.
	 * @throws FileUploadException
	 *             if there is a problem parsing the request
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
