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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.MissingResourceException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.repository.api.ContentTypeHelper;
import org.eclipse.dirigible.repository.api.ICollection;
import org.eclipse.dirigible.repository.api.IEntity;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.logging.Logger;

/**
 * Repository Service gives full access to the Dirigible Repository API.
 */
public class RepositoryServlet extends RegistryServlet {

	private static final String REQUEST_PROCESSING_FAILED_S = Messages.getString("RepositoryServlet.REQUEST_PROCESSING_FAILED_S"); //$NON-NLS-1$

	private static final String THERE_IS_AN_EXISTING_COLLECTION_AT_THE_SAME_LOCATION = Messages
			.getString("RepositoryServlet.THERE_IS_AN_EXISTING_COLLECTION_AT_THE_SAME_LOCATION"); //$NON-NLS-1$

	private static final String THERE_IS_AN_EXISTING_RESOURCE_AT_THE_SAME_LOCATION_USE_PUT_METHOD_FOR_UPDATE = Messages
			.getString("RepositoryServlet.THERE_IS_AN_EXISTING_RESOURCE_AT_THE_SAME_LOCATION_USE_PUT_METHOD_FOR_UPDATE"); //$NON-NLS-1$

	private static final long serialVersionUID = 726309327921007381L;

	private static final Logger logger = Logger.getLogger(RepositoryServlet.class);

	@Override
	protected String getRepositoryPathPrefix(HttpServletRequest req) {
		return "";
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String repositoryPath = null;
		final OutputStream out = response.getOutputStream();
		try {
			repositoryPath = extractRepositoryPath(request);
			IEntity entity = getEntity(repositoryPath, request);
			byte[] data;
			if (entity == null) {
				ByteArrayOutputStream buff = new ByteArrayOutputStream();
				IOUtils.copy(request.getInputStream(), buff);
				data = buff.toByteArray();
				String contentType = request.getContentType();
				if (contentType == null) {
					contentType = "text/plain"; //$NON-NLS-1$
				}
				boolean isBinary = ContentTypeHelper.isBinary(contentType);
				getRepository(request).createResource(repositoryPath, data, isBinary, contentType);
			} else {
				if (entity instanceof IResource) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							THERE_IS_AN_EXISTING_RESOURCE_AT_THE_SAME_LOCATION_USE_PUT_METHOD_FOR_UPDATE);
				} else if (entity instanceof ICollection) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, THERE_IS_AN_EXISTING_COLLECTION_AT_THE_SAME_LOCATION);
				}
			}
		} catch (IllegalArgumentException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		} catch (MissingResourceException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_NO_CONTENT, ex.getMessage());
		} finally {
			out.flush();
			out.close();
		}

	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String repositoryPath = null;
		final OutputStream out = response.getOutputStream();
		try {
			repositoryPath = extractRepositoryPath(request);
			IEntity entity = getEntity(repositoryPath, request);
			if (entity == null) {
				doPost(request, response);
			} else {
				if (entity instanceof IResource) {
					ByteArrayOutputStream buff = new ByteArrayOutputStream();
					IOUtils.copy(request.getInputStream(), buff);
					byte[] data = buff.toByteArray();
					IResource resource = (IResource) entity;
					resource.setContent(data);
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, THERE_IS_AN_EXISTING_COLLECTION_AT_THE_SAME_LOCATION);
				}
			}
		} catch (IllegalArgumentException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		} catch (MissingResourceException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_NO_CONTENT, ex.getMessage());
		} finally {
			out.flush();
			out.close();
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String repositoryPath = null;
		final OutputStream out = response.getOutputStream();
		try {
			repositoryPath = extractRepositoryPath(request);
			IEntity entity = getEntity(repositoryPath, request);
			if (entity != null) {
				getRepository(request).removeResource(repositoryPath);
			}
		} catch (IllegalArgumentException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
		} catch (MissingResourceException ex) {
			logger.error(String.format(REQUEST_PROCESSING_FAILED_S, repositoryPath) + ex.getMessage(), ex);
			response.sendError(HttpServletResponse.SC_NO_CONTENT, ex.getMessage());
		} finally {
			out.flush();
			out.close();
		}
	}

}
