/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.api.v3.http;

import java.util.ArrayList;
import java.util.Iterator;
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
 * Java facade for working uploading files.
 */
public class HttpUploadFacade implements IScriptingFacade {

	/** The Constant NO_VALID_REQUEST. */
	private static final String NO_VALID_REQUEST = "Trying to use HTTP Upload Facade without a valid Request";

	/** The Constant logger. */
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

	/**
	 * Converts header names iterator object to list object.
	 *
	 * @param headerNames header names iterator
	 * @return A list of String elements which represent the name of the multipart request headers.
	 */
	public static final List<String> headerNamesToList(Iterator<String> headerNames) {
		List<String> headerNamesList = new ArrayList<String>();
		while (headerNames.hasNext()) {
			headerNamesList.add(headerNames.next());
		}
		return headerNamesList;
	}

}
