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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.IRepository;
import org.eclipse.dirigible.repository.logging.Logger;

public class ContentExporterServlet extends BaseContentServlet {
	
	private static final long serialVersionUID = 5798183051027211544L;

	private static final Logger logger = Logger.getLogger(ContentExporterServlet.class);
	
	private static final String GUID = "guid"; //$NON-NLS-1$

	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// put guid in the session
		request.getSession().setAttribute(GUID, createGUID()); //$NON-NLS-1$

		byte[] zippedContent = getContentFromRepository(request);

		sendZip(request, response, zippedContent);
	}

	/**
	 * Return the constructed zip in servlet response
	 * 
	 * @param response
	 * @param tmpFile
	 * @param request
	 */
	private void sendZip(HttpServletRequest request, HttpServletResponse response, byte[] content) {
		String fileName = null;

		fileName = defaultFileName(request) + ".zip";

		response.setContentType("application/zip"); //$NON-NLS-1$
		response.setHeader("Content-Disposition", "attachment;filename=\"" //$NON-NLS-1$ //$NON-NLS-2$
				+ fileName + "\""); //$NON-NLS-1$
		try {
			BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
			ByteArrayInputStream fis = new ByteArrayInputStream(content);
			int len;
			byte[] buf = new byte[1024];
			while ((len = fis.read(buf)) > 0) {
				bos.write(buf, 0, len);
			}
			bos.close();
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private String defaultFileName(HttpServletRequest request) {
		String fileName;
		String guid = "".equals(getGUID(request)) ? "" : "." //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				+ getGUID(request);
		fileName = COM_SAP_DIRIGIBLE_RUNTIME + guid;
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
			zippedContent = repository.exportZip(DEFAULT_PATH_FOR_EXPORT, true);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return zippedContent;
	}

	/**
	 * Create guid. Currently timestamp.
	 * 
	 * @return
	 */
	private String createGUID() {
		// SimpleDateFormat sdfDate = new
		// SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd-HHmmss"); //$NON-NLS-1$
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	private String getGUID(HttpServletRequest request) {
		return (String) request.getSession().getAttribute(GUID);
	}

}
