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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.ext.security.IRoles;
import org.eclipse.dirigible.repository.logging.Logger;
import org.eclipse.dirigible.runtime.PermissionsUtils;

/**
 * Exports the current content of the Registry
 */
public class ContentExporterServlet extends ContentBaseServlet {

	private static final long serialVersionUID = 5798183051027211544L;

	private static final Logger logger = Logger.getLogger(ContentExporterServlet.class);

	private static final String GUID = "guid"; //$NON-NLS-1$
	private static final String DEFAULT_PATH_FOR_EXPORT = IRepositoryPaths.REGISTRY_DEPLOY_PATH;
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String DATE_FORMAT = "yyyyMMdd-HHmmss"; //$NON-NLS-1$

	static final String UNKNOWN_HOST = "unknown_host";

	static final String UNDERSCORE = "_"; //$NON-NLS-1$

	protected String getExportFilePrefix() {
		StringBuilder buff = new StringBuilder();
		buff.append(IRepositoryPaths.REGISTRY).append(UNDERSCORE);
		try {
			buff.append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			buff.append(UNKNOWN_HOST);
		}
		return buff.toString();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (!PermissionsUtils.isUserInRole(request, IRoles.ROLE_OPERATOR)) {
			String err = String.format(PermissionsUtils.PERMISSION_ERR, "Export");
			logger.error(err);
			throw new ServletException(err);
		}

		// put guid in the session
		request.setAttribute(GUID, createGUID());

		try {
			byte[] zippedContent = getContentFromRepository(request);

			sendZip(request, response, zippedContent);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * Return the constructed zip in servlet response
	 *
	 * @param response
	 * @param tmpFile
	 * @param request
	 * @throws IOException
	 */
	private void sendZip(HttpServletRequest request, HttpServletResponse response, byte[] content) throws IOException {
		String fileName = null;

		fileName = defaultFileName(request) + ".zip";

		response.setContentType("application/zip"); //$NON-NLS-1$
		response.setHeader("Content-Disposition", "attachment;filename=\"" //$NON-NLS-1$ //$NON-NLS-2$
				+ fileName + "\""); //$NON-NLS-1$

		BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
		ByteArrayInputStream fis = new ByteArrayInputStream(content);
		int len;
		byte[] buf = new byte[1024];
		while ((len = fis.read(buf)) > 0) {
			bos.write(buf, 0, len);
		}
		bos.close();
		fis.close();
	}

	private String defaultFileName(HttpServletRequest request) {
		String fileName;
		String guid = EMPTY.equals(getGUID(request)) ? EMPTY : getGUID(request);
		fileName = getExportFilePrefix() + UNDERSCORE + guid;
		return fileName;
	}

	/**
	 * Extract the Dirigible project as a zip from the repository.
	 *
	 * @param request
	 * @return
	 */
	private byte[] getContentFromRepository(HttpServletRequest request) {
		byte[] zippedContent = null;
		try {
			IRepository repository = getRepository(request);
			zippedContent = repository.exportZip(getDefaultPathForExport(), true);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return zippedContent;
	}

	protected String getDefaultPathForExport() {
		return DEFAULT_PATH_FOR_EXPORT;
	}

	/**
	 * Create guid. Currently timestamp.
	 *
	 * @return
	 */
	private String createGUID() {
		// SimpleDateFormat sdfDate = new
		// SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");
		SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	private String getGUID(HttpServletRequest request) {
		return (String) request.getAttribute(GUID);
	}

}
