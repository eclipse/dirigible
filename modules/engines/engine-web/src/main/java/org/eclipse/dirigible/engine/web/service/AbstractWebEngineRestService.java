/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.web.service;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.dirigible.commons.api.helpers.ContentTypeHelper;
import org.eclipse.dirigible.commons.api.service.AbstractRestService;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.commons.config.ResourcesCache;
import org.eclipse.dirigible.commons.config.ResourcesCache.Cache;
import org.eclipse.dirigible.engine.web.processor.WebEngineProcessor;
import org.eclipse.dirigible.repository.api.IRepositoryStructure;
import org.eclipse.dirigible.repository.api.IResource;
import org.eclipse.dirigible.repository.api.RepositoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Front facing REST service serving the raw web content from the registry/public space.
 */
public abstract class AbstractWebEngineRestService extends AbstractRestService implements IRestService {

	private static final Cache WEB_CACHE = ResourcesCache.getWebCache();

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebEngineRestService.class);

	private static final String INDEX_HTML = "index.html";

	@Inject
	private WebEngineProcessor processor;

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	protected HttpServletRequest getRequest() {
		return request;
	}

	protected HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Gets the resource.
	 *
	 * @param path
	 *            the path
	 * @return the resource
	 */
	public Response getResource(@PathParam("path") String path) {
		if ("".equals(path.trim())) {
			return Response.status(Status.FORBIDDEN).entity("Listing of web folders is forbidden.").build();
		} else if (path.trim().endsWith(IRepositoryStructure.SEPARATOR)) {
			return getResourceByPath(path + INDEX_HTML);
		}
		Response resourceResponse = getResourceByPath(path);
		if (!Configuration.isProductiveIFrameEnabled()) {
			resourceResponse.getHeaders().add("X-Frame-Options", "Deny");
		}
		return resourceResponse;
	}

	/**
	 * Gets the resource by path.
	 *
	 * @param path
	 *            the path
	 * @return the resource by path
	 */
	private Response getResourceByPath(String path) {
		if (isCached(path)) {
			return sendResourceNotModified();
		}

		if (processor.existResource(path)) {
			IResource resource = processor.getResource(path);
			String contentType = resource.getContentType();
			return sendResource(path, resource.isBinary(), resource.getContent(), contentType);
		}

		String errorMessage = "Resource not found: " + path;
		try {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
				return sendResource(path, ContentTypeHelper.isBinary(contentType), content, contentType);
			}
		} catch (RepositoryNotFoundException e) {
			throw new RepositoryNotFoundException(errorMessage, e);
		}
		throw new RepositoryNotFoundException(errorMessage);
	}

	private Response sendResourceNotModified() {
		return Response
				.notModified()
				.header("ETag", getTag())
				.build();
	}

	private Response sendResource(String path, boolean isBinary, byte[] content, String contentType) {
		String tag = cacheResource(path);
		Object responseContent = isBinary ? content : new String(content, StandardCharsets.UTF_8);
		return Response
				.ok(responseContent)
				.type(contentType)
				.header("Cache-Control", "public, must-revalidate, max-age=0")
				.header("ETag", tag)
				.build();
	}

	private String cacheResource(String path) {
		String tag = WEB_CACHE.generateTag();
		WEB_CACHE.setTag(path, tag);
		return tag;
	}

	private boolean isCached(String path) {
		String tag = getTag();
		String cachedTag = WEB_CACHE.getTag(path);
		return tag == null || cachedTag == null ? false : tag.equals(cachedTag); 
		
	}

	private String getTag() {
		return getRequest().getHeader("If-None-Match");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
