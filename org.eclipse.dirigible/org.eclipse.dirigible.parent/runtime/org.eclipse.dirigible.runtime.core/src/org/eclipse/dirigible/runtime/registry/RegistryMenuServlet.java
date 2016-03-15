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
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IRepositoryPaths;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Servlet implementation class RegistryMenuServlet
 * Returns the customizable menu for the Registry UI
 */
public class RegistryMenuServlet extends AbstractRegistryServlet {

	private static final String MENU_ERROR_FALLBACK = "[{\"name\": \"Error\",\"link\":\"http://bugs.dirigible.io\"}]";

	private static final Logger logger = Logger.getLogger(RegistryMenuServlet.class);

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		OutputStream out = response.getOutputStream();
		response.setContentType(ContentTypeHelper.APPLICATION_JSON);
		try {
			final IEntity entity = getEntity(IRepositoryPaths.REPOSITORY_MENU, request);
			byte[] data;
			if (entity != null) {
				if (entity instanceof IResource) {
					data = buildResourceData(entity, request, response);
					sendData(out, data);
					return;
				}
			}
			sendData(out, MENU_ERROR_FALLBACK.getBytes());
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
