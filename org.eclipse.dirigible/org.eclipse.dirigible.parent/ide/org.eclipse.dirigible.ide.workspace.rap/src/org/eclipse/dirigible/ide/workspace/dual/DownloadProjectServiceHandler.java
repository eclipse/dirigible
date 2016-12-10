/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.ide.workspace.dual;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;

/**
 * Download Project handler in the RAP environment
 */
public class DownloadProjectServiceHandler implements ServiceHandler {

	private static final String SLASH = "/"; //$NON-NLS-1$

	private static final String ZIP = ".zip"; //$NON-NLS-1$

	private static final String EQ = "="; //$NON-NLS-1$

	private static final String AMP = "&"; //$NON-NLS-1$

	private static final String CONTENT_DISPOSITION_KEY = "Content-Disposition"; //$NON-NLS-1$

	private static final String CONTENT_DISPOSITION_PARAM = "attachment; filename=\"%s\""; //$NON-NLS-1$

	private static final String ERROR_TXT = "error.txt"; //$NON-NLS-1$

	private static final String TEXT_PLAIN = "text/plain"; //$NON-NLS-1$

	private static final String APPLICATION_ZIP = "application/zip"; //$NON-NLS-1$

	private static final String FILENAME_PARAM = "filename"; //$NON-NLS-1$

	private static final String ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION = Messages.DownloadProjectServiceHandler_ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION;

	static final String SERVICE_HANDLER_ID = "org.eclipse.dirigible.ide.workspace.wizard.project.export.DownloadProjectServiceHandler"; //$NON-NLS-1$

	private static final Logger logger = Logger.getLogger(DownloadProjectServiceHandler.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		// Which file to download?
		String fileName = request.getParameter(FILENAME_PARAM);
		fileName = StringEscapeUtils.escapeHtml(fileName);
		fileName = StringEscapeUtils.escapeJavaScript(fileName);
		// Get the file content
		byte[] download;
		try {
			download = RepositoryDataStore.getByteArrayData(fileName);
			// Send the file in the response
			response.setContentType(APPLICATION_ZIP);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			download = (ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION + fileName).getBytes();
			// Send the file in the response
			response.setContentType(TEXT_PLAIN);
			fileName = ERROR_TXT;
		}
		response.setContentLength(download.length);
		String contentDisposition = String.format(CONTENT_DISPOSITION_PARAM, fileName);
		response.setHeader(CONTENT_DISPOSITION_KEY, contentDisposition);
		response.getOutputStream().write(download);

	}

	/**
	 * Generator for the download URL
	 * 
	 * @param token
	 * @return the URL
	 */
	public static String getUrl(String token) {
		ServiceManager manager = RWT.getServiceManager();
		String rootURL = manager.getServiceHandlerUrl(SERVICE_HANDLER_ID);
		StringBuffer url = new StringBuffer();
		url.append(rootURL);
		url.append(AMP);
		url.append(FILENAME_PARAM).append(EQ).append(token).append(ZIP);
		int relativeIndex = url.lastIndexOf(SLASH);
		if (relativeIndex > -1) {
			url.delete(0, relativeIndex + 1);
		}
		return RWT.getResponse().encodeURL(url.toString());
	}
}
