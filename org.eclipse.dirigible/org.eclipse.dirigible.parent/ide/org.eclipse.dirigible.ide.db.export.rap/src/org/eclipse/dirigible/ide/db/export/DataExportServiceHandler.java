/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.db.export;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableExporter;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;

public class DataExportServiceHandler implements ServiceHandler {

	private static final String DSV_EXTENSION = ".dsv";
	private static final String DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV = Messages.DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV;
	static final String DataExportServiceHandler_SERVICE_HANDLER_ID = "org.eclipse.dirigible.ide.db.export.DataExportServiceHandler";

	private static final Logger logger = Logger.getLogger(DataExportServiceHandler.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String fileName = request.getParameter("filename");//$NON-NLS-1$
		String tableName = fileName.substring(0, fileName.lastIndexOf(DSV_EXTENSION)).toUpperCase();
		byte[] download;

		try {
			DBTableExporter dataFinder = new DBTableExporter(DataSourceFacade.getInstance().getDataSource(request));
			dataFinder.setTableName(tableName);
			download = dataFinder.getTableData().getBytes();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			download = (DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV + tableName).getBytes();
			tableName = "error.txt"; //$NON-NLS-1$
		}

		String contentDisposition = "attachment; filename=\"" + fileName + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		response.setContentType("text/plain");//$NON-NLS-1$
		response.setHeader("Content-Disposition", contentDisposition); //$NON-NLS-1$
		response.getOutputStream().write(download);
	}

	public static String getUrl(String tableName) {

		ServiceManager manager = RWT.getServiceManager();
		String rootURL = manager.getServiceHandlerUrl(DataExportServiceHandler_SERVICE_HANDLER_ID);
		StringBuffer url = new StringBuffer();
		url.append(rootURL);
		url.append("&"); //$NON-NLS-1$
		url.append("filename").append("=").append(tableName.toLowerCase()).append(DSV_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$
		int relativeIndex = url.lastIndexOf("/"); //$NON-NLS-1$

		if (relativeIndex > -1) {
			url.delete(0, relativeIndex + 1);
		}

		return RWT.getResponse().encodeURL(url.toString());
	}
}
