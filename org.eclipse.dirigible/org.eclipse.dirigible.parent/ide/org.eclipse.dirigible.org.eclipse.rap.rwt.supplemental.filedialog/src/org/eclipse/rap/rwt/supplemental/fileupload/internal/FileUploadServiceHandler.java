/*******************************************************************************
 * Copyright (c) 2002, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Austin Riddle (Texas Center for Applied Technology) - migration to support
 *                  compatibility with varied upload widget implementations
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.supplemental.fileupload.internal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.ServiceManager;
import org.eclipse.rap.rwt.supplemental.fileupload.FileUploadHandler;

public final class FileUploadServiceHandler implements ServiceHandler {
	private static final String CONTENT_MUST_BE_IN_MULTIPART_TYPE = Messages.FileUploadServiceHandler_CONTENT_MUST_BE_IN_MULTIPART_TYPE;

	private static final String INVALID_OR_MISSING_TOKEN = Messages.FileUploadServiceHandler_INVALID_OR_MISSING_TOKEN;

	private static final String ONLY_POST_REQUESTS_ALLOWED = Messages.FileUploadServiceHandler_ONLY_POST_REQUESTS_ALLOWED;

	private static final String PARAMETER_TOKEN = "token"; //$NON-NLS-1$

	static final String SERVICE_HANDLER_ID = "org.eclipse.rap.rwt.supplemental.fileupload.internal.FileUploadServiceHandler"; //$NON-NLS-1$

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// TODO [rst] Revise: does this double security make it any more secure?
		// Ignore requests to this service handler without a valid session for
		// security reasons
		boolean hasSession = request.getSession(false) != null;
		if (hasSession) {
			String token = request.getParameter(PARAMETER_TOKEN);
			FileUploadHandler registeredHandler = FileUploadHandlerStore
					.getInstance().getHandler(token);
			if (registeredHandler == null) {
				String message = INVALID_OR_MISSING_TOKEN;
				response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
			} else if (!"POST".equals(request.getMethod().toUpperCase())) { //$NON-NLS-1$
				String message = ONLY_POST_REQUESTS_ALLOWED;
				response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
						message);
			} else if (!ServletFileUpload.isMultipartContent(request)) {
				String message = CONTENT_MUST_BE_IN_MULTIPART_TYPE;
				response.sendError(
						HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
			} else {
				FileUploadProcessor processor = new FileUploadProcessor(
						registeredHandler);
				processor.handleFileUpload(request, response);
			}
		}
	}

	public static String getUrl(String token) {
		ServiceManager manager = RWT.getServiceManager();
		String rootURL = manager.getServiceHandlerUrl(SERVICE_HANDLER_ID);
		StringBuffer url = new StringBuffer();
		url.append(rootURL);
		url.append("&"); //$NON-NLS-1$
		url.append(PARAMETER_TOKEN).append("=").append(token); //$NON-NLS-1$
		int relativeIndex = url.lastIndexOf("/"); //$NON-NLS-1$
		if (relativeIndex > -1) {
			url.delete(0, relativeIndex + 1);
		}
		return RWT.getResponse().encodeURL(url.toString());
	}
}
