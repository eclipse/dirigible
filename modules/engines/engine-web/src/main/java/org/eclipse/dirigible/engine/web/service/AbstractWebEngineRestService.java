/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.engine.web.service;

import java.nio.charset.StandardCharsets;

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

	/** The Constant WEB_CACHE. */
	private static final Cache WEB_CACHE = ResourcesCache.getWebCache();

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractWebEngineRestService.class);

	/** The Constant INDEX_HTML. */
	private static final String INDEX_HTML = "index.html";

	/** The processor. */
	private WebEngineProcessor processor = new WebEngineProcessor();

	/** The request. */
	@Context
	private HttpServletRequest request;

	/** The response. */
	@Context
	private HttpServletResponse response;

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
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
			if (resource == null) {
				throw new RepositoryNotFoundException("Resource requested is not exposed.");
			}
			String contentType = resource.getContentType();
			return sendResource(path, resource.isBinary(), resource.getContent(), contentType);
		}

		String errorMessage = "Resource not found: " + path;
		try {
			byte[] content = processor.getResourceContent(path);
			if (content != null) {
				String contentType = ContentTypeHelper.getContentType(ContentTypeHelper.getExtension(path));
				return sendResource(path, ContentTypeHelper.isBinary(contentType), content, contentType);
			} else {
				throw new RepositoryNotFoundException("Resource requested is not exposed.");
			}
		} catch (RepositoryNotFoundException e) {
			throw new RepositoryNotFoundException(errorMessage, e);
		}
	}

	/**
	 * Send resource not modified.
	 *
	 * @return the response
	 */
	private Response sendResourceNotModified() {
		return Response
				.notModified()
				.header("ETag", getTag())
				.build();
	}

	/**
	 * Send resource.
	 *
	 * @param path the path
	 * @param isBinary the is binary
	 * @param content the content
	 * @param contentType the content type
	 * @return the response
	 */
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

	/**
	 * Cache resource.
	 *
	 * @param path the path
	 * @return the string
	 */
	private String cacheResource(String path) {
		String tag = WEB_CACHE.generateTag();
		WEB_CACHE.setTag(path, tag);
		return tag;
	}

	/**
	 * Checks if is cached.
	 *
	 * @param path the path
	 * @return true, if is cached
	 */
	private boolean isCached(String path) {
		String tag = getTag();
		String cachedTag = WEB_CACHE.getTag(path);
		return tag == null || cachedTag == null ? false : tag.equals(cachedTag); 
		
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	private String getTag() {
		return getRequest().getHeader("If-None-Match");
	}

	/**
	 * Gets the logger.
	 *
	 * @return the logger
	 */
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.dirigible.commons.api.service.AbstractRestService#getLogger()
	 */
	@Override
	protected Logger getLogger() {
		return logger;
	}

}
