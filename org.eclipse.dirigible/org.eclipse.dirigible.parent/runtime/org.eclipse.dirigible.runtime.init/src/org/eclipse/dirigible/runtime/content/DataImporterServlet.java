/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
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
import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableImporter;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.PermissionsUtils;

/**
 * Imports the provided content into the Registry
 */
public class DataImporterServlet extends ContentBaseServlet {

	private static final long serialVersionUID = 5844468087553458293L;

	private static final Logger logger = Logger.getLogger(DataImporterServlet.class);

	private static final String PARAMETER_TABLE = "table";

	public static final String PARAMETER_TABLE_ERR = "Parameter 'table' is not present while importing a single data file. Use .../data-import?table=TABLE_XXX";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!PermissionsUtils.isUserInRole(request, IRoles.ROLE_OPERATOR)) {
			String err = String.format(PermissionsUtils.PERMISSION_ERR, "Data Import");
			logger.error(err);
			throw new ServletException(err);
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		try {
			if (isMultipart) {
				FileItemFactory factory = new DiskFileItemFactory();
				ServletFileUpload upload = new ServletFileUpload(factory);

				try {
					List<FileItem> items = upload.parseRequest(request);
					logger.debug("Importing multiple data files...");
					for (FileItem fileItem : items) {
						logger.debug("Importing " + fileItem.getFieldName());
						InputStream in = fileItem.getInputStream();
						importData(fileItem.getFieldName(), in, request);
						logger.debug("Data imported.");
					}
				} catch (FileUploadException e) {
					logger.error(e.getMessage(), e);
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else {
				logger.debug("Importing single data file...");
				String tableName = request.getParameter(PARAMETER_TABLE);
				if ((tableName == null) || "".equals(tableName)) {
					logger.error(PARAMETER_TABLE_ERR);
					throw new ServletException(PARAMETER_TABLE_ERR);
				}
				InputStream in = request.getInputStream();
				importData(tableName + ".dsv", in, request);
				logger.debug("Data imported.");

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Imports Table Data into the Database
	 *
	 * @param content
	 * @param request
	 * @param override
	 * @throws Exception
	 */
	public void importData(String tableName, InputStream content, HttpServletRequest request) throws Exception {
		byte[] data = IOUtils.toByteArray(content);
		DBTableImporter dataInserter = new DBTableImporter(DataSourceFacade.getInstance().getDataSource(request), data, tableName);
		dataInserter.insert();
	}

}
