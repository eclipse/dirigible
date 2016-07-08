/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

package org.eclipse.dirigible.runtime.registry;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.ICommonConstants;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Servlet implementation class RegistryMenuServlet
 * Returns the customizable menu for the Registry UI
 */
public class RegistryHomeServlet extends AbstractRegistryServlet {

	private static final long serialVersionUID = 6417395529878256611L;

	private static final Logger logger = Logger.getLogger(RegistryHomeServlet.class);

	private static final String PARAM_HOME_URL = ICommonConstants.INIT_PARAM_HOME_URL;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		OutputStream out = response.getOutputStream();
		response.setContentType(ContentTypeHelper.TEXT_PLAIN);
		try {
			try {
				final IEntity entity = getEntity(IRepositoryPaths.REPOSITORY_HOME_URL, request);
				byte[] data;
				if (entity != null) {
					if (entity instanceof IResource) {
						data = buildResourceData(entity, request, response);
						sendData(out, data);
						return;
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			String homeUrl = System.getProperty(PARAM_HOME_URL);
			if ((homeUrl != null) && !ICommonConstants.EMPTY_STRING.equals(homeUrl.trim())) {
				sendData(out, homeUrl.getBytes());
				return;
			}

			sendData(out, IRepositoryPaths.INDEX_HTML_FALLBACK.getBytes());
		} finally {
			out.flush();
			out.close();
		}
	}

	protected byte[] buildResourceData(final IEntity entity, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		byte[] data = new byte[] {};
		data = readResourceData((IResource) entity);

		return data;
	}

}
