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

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.datasource.DataSourceFacade;
import org.eclipse.dirigible.repository.ext.db.transfer.DBTableExporter;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;

/**
 * Data Export handler in the RAP environment
 */
public class DataExportServiceHandler implements ServiceHandler {

	private static final String SLASH = "/"; //$NON-NLS-1$
	private static final String EQ = "="; //$NON-NLS-1$
	private static final String AMP = "&"; //$NON-NLS-1$
	private static final String CONTENT_DISPOSITION_KEY = "Content-Disposition"; //$NON-NLS-1$
	private static final String TEXT_PLAIN = "text/plain"; //$NON-NLS-1$
	private static final String CONTENT_DISPOSITION_PARAM = "attachment; filename=\"%s\""; //$NON-NLS-1$
	private static final String FILE_ERROR_TXT = "error.txt"; //$NON-NLS-1$
	private static final String FILENAME_PARAM = "filename"; //$NON-NLS-1$
	private static final String DSV_EXTENSION = ".dsv"; //$NON-NLS-1$
	private static final String DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV = Messages.DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV;
	static final String DataExportServiceHandler_SERVICE_HANDLER_ID = "org.eclipse.dirigible.ide.db.export.DataExportServiceHandler";

	private static final Logger logger = Logger.getLogger(DataExportServiceHandler.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String fileName = request.getParameter(FILENAME_PARAM);
		fileName = StringEscapeUtils.escapeHtml(fileName);
		fileName = StringEscapeUtils.escapeJavaScript(fileName);
		String tableName = fileName.substring(0, fileName.lastIndexOf(DSV_EXTENSION)).toUpperCase();
		byte[] download;

		try {
			DBTableExporter dataFinder = new DBTableExporter(DataSourceFacade.getInstance().getDataSource(request));
			dataFinder.setTableName(tableName);
			download = dataFinder.getTableData().getBytes(ICommonConstants.UTF8);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			download = (DataExportServiceHandler_ERROR_WHILE_EXPORTING_DSV + tableName).getBytes();
			tableName = FILE_ERROR_TXT;
		}

		String contentDisposition = String.format(CONTENT_DISPOSITION_PARAM, fileName);
		response.setContentType(TEXT_PLAIN);
		response.setHeader(CONTENT_DISPOSITION_KEY, contentDisposition);
		response.getOutputStream().write(download);
	}

	/**
	 * Generate the download URL
	 *
	 * @param tableName
	 * @return the URL
	 */
	public static String getUrl(String tableName) {

		ServiceManager manager = RWT.getServiceManager();
		String rootURL = manager.getServiceHandlerUrl(DataExportServiceHandler_SERVICE_HANDLER_ID);
		StringBuffer url = new StringBuffer();
		url.append(rootURL);
		url.append(AMP);
		url.append(FILENAME_PARAM).append(EQ).append(tableName.toLowerCase()).append(DSV_EXTENSION);
		int relativeIndex = url.lastIndexOf(SLASH);

		if (relativeIndex > -1) {
			url.delete(0, relativeIndex + 1);
		}

		return RWT.getResponse().encodeURL(url.toString());
	}
}
