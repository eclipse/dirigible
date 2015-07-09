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

package org.eclipse.dirigible.ide.workspace.dual;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;

import org.eclipse.dirigible.repository.logging.Logger;

public class DownloadProjectServiceHandler implements ServiceHandler {

	private static final String ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION = Messages.DownloadProjectServiceHandler_ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION;

	static final String SERVICE_HANDLER_ID = "org.eclipse.dirigible.ide.workspace.wizard.project.export.DownloadProjectServiceHandler"; //$NON-NLS-1$

	private static final Logger logger = Logger
			.getLogger(DownloadProjectServiceHandler.class);

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Which file to download?
		String fileName = request.getParameter("filename"); //$NON-NLS-1$
		// Get the file content
		byte[] download;
		try {
			download = RepositoryDataStore.getByteArrayData(fileName);
			// Send the file in the response
			response.setContentType("application/zip"); //$NON-NLS-1$
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			download = (ERROR_WHILE_GETTING_CONTENT_FROM_LOCATION + fileName).getBytes();
			// Send the file in the response
			response.setContentType("text/plain"); //$NON-NLS-1$
			fileName = "error.txt"; //$NON-NLS-1$
		}
		response.setContentLength(download.length);
		String contentDisposition = "attachment; filename=\"" + fileName + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		response.setHeader("Content-Disposition", contentDisposition); //$NON-NLS-1$
		response.getOutputStream().write(download);

	}

	public static String getUrl(String token) {
		ServiceManager manager = RWT.getServiceManager();
		String rootURL = manager.getServiceHandlerUrl(SERVICE_HANDLER_ID);
		StringBuffer url = new StringBuffer();
		url.append(rootURL);
		url.append("&"); //$NON-NLS-1$
		url.append("filename").append("=").append(token).append(".zip"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		int relativeIndex = url.lastIndexOf("/"); //$NON-NLS-1$
		if (relativeIndex > -1) {
			url.delete(0, relativeIndex + 1);
		}
		return RWT.getResponse().encodeURL(url.toString());
	}
}
