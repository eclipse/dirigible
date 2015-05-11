/******************************************************************************* 
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.eclipse.dirigible.repository.logging.Logger;

public class ContentImporterServlet extends BaseContentServlet {

	private static final String PARAMETER_OVERRIDE = "override";

	private static final String HEADER_OVERRIDE = "override";

	private static final long serialVersionUID = 5844468087553458293L;

	private static final Logger logger = Logger
			.getLogger(ContentImporterServlet.class);

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				List<FileItem> items = upload.parseRequest(request);
				logger.debug("Importing multiple content...");
				for (FileItem fileItem : items) {
					logger.debug("Importing " + fileItem.getFieldName());
					InputStream in = fileItem.getInputStream();
					boolean override = Boolean.parseBoolean(request.getParameter(PARAMETER_OVERRIDE)) || Boolean.parseBoolean(request.getHeader(HEADER_OVERRIDE));
					importZipAndUpdate(in, request, override);
					logger.debug("Content imported.");
				}
			} catch (FileUploadException e) {
				logger.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			logger.debug("Importing single content...");
			InputStream in = request.getInputStream();
			importZipAndUpdate(in, request);
			logger.debug("Content imported.");

		}
	}

}
