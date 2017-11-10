/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

package org.eclipse.dirigible.api.v3.http;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpRequestFacade.
 */
public class HttpRequestFacade implements IScriptingFacade {

	/** The Constant ATTRIBUTE_REST_RESOURCE_PATH. */
	public static final String ATTRIBUTE_REST_RESOURCE_PATH = "dirigible-rest-resource-path";

	/** The Constant NO_VALID_REQUEST. */
	private static final String NO_VALID_REQUEST = "Trying to use HTTP Request Facade without a valid Request";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestFacade.class);

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	static final HttpServletRequest getRequest() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			return (HttpServletRequest) ThreadContextFacade.get(HttpServletRequest.class.getCanonicalName());
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public static final boolean isValid() {
		HttpServletRequest request = getRequest();
		return request != null;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public static final String getMethod() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getMethod();
	}

	/**
	 * Gets the remote user.
	 *
	 * @return the remote user
	 */
	public static final String getRemoteUser() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteUser();
	}

	/**
	 * Gets the path info.
	 *
	 * @return the path info
	 */
	public static final String getPathInfo() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getPathInfo();
	}

	/**
	 * Gets the path translated.
	 *
	 * @return the path translated
	 */
	public static final String getPathTranslated() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getPathTranslated();
	}

	/**
	 * Gets the header.
	 *
	 * @param name the name
	 * @return the header
	 */
	public static final String getHeader(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getHeader(name);
	}

	/**
	 * Checks if is user in role.
	 *
	 * @param role the role
	 * @return true, if is user in role
	 */
	public static final boolean isUserInRole(String role) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.isUserInRole(role);
	}

	/**
	 * Gets the attribute.
	 *
	 * @param name the name
	 * @return the attribute
	 */
	public static final String getAttribute(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getAttribute(name) != null ? request.getAttribute(name).toString() : null;
	}

	/**
	 * Gets the auth type.
	 *
	 * @return the auth type
	 */
	public static final String getAuthType() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getAuthType();
	}

	/**
	 * Gets the cookies.
	 *
	 * @return the cookies
	 */
	public static final String getCookies() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getCookies());
	}

	/**
	 * Gets the attribute names.
	 *
	 * @return the attribute names
	 */
	public static final String getAttributeNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getAttributeNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	/**
	 * Gets the character encoding.
	 *
	 * @return the character encoding
	 */
	public static final String getCharacterEncoding() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getCharacterEncoding();
	}

	/**
	 * Gets the content length.
	 *
	 * @return the content length
	 */
	public static final int getContentLength() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContentLength();
	}

	/**
	 * Gets the headers.
	 *
	 * @param name the name
	 * @return the headers
	 */
	public static final String getHeaders(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getHeaders(name));
		return GsonHelper.GSON.toJson(list.toArray());
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public static final String getContentType() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContentType();
	}

	/**
	 * Gets the bytes.
	 *
	 * @return the bytes
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getBytes() throws IOException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return BytesHelper.bytesToJson(IOUtils.toByteArray(request.getInputStream()));
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final String getText() throws IOException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		byte[] bytes = IOUtils.toByteArray(request.getInputStream());
		String charset = (request.getCharacterEncoding() != null) ? request.getCharacterEncoding() : StandardCharsets.UTF_8.name();
		return new String(bytes, charset);
	}

	/**
	 * Gets the parameter.
	 *
	 * @param name the name
	 * @return the parameter
	 */
	public static final String getParameter(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getParameter(name);
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public static final String getParameters() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getParameterMap());
	}

	/**
	 * Gets the resource path.
	 *
	 * @return the resource path
	 */
	public static final String getResourcePath() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		Object resourcePathParameter = request.getAttribute(ATTRIBUTE_REST_RESOURCE_PATH);
		return (resourcePathParameter != null ? resourcePathParameter.toString() : "");
	}

	/**
	 * Gets the header names.
	 *
	 * @return the header names
	 */
	public static final String getHeaderNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getHeaderNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	/**
	 * Gets the parameter names.
	 *
	 * @return the parameter names
	 */
	public static final String getParameterNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getParameterNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	/**
	 * Gets the parameter values.
	 *
	 * @param name the name
	 * @return the parameter values
	 */
	public static final String getParameterValues(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getParameterValues(name));
	}

	/**
	 * Gets the protocol.
	 *
	 * @return the protocol
	 */
	public static final String getProtocol() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getProtocol();
	}

	/**
	 * Gets the scheme.
	 *
	 * @return the scheme
	 */
	public static final String getScheme() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getScheme();
	}

	/**
	 * Gets the context path.
	 *
	 * @return the context path
	 */
	public static final String getContextPath() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContextPath();
	}

	/**
	 * Gets the server name.
	 *
	 * @return the server name
	 */
	public static final String getServerName() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServerName();
	}

	/**
	 * Gets the server port.
	 *
	 * @return the server port
	 */
	public static final int getServerPort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServerPort();
	}

	/**
	 * Gets the query string.
	 *
	 * @return the query string
	 */
	public static final String getQueryString() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getQueryString();
	}

	/**
	 * Gets the remote address.
	 *
	 * @return the remote address
	 */
	public static final String getRemoteAddress() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteAddr();
	}

	/**
	 * Gets the remote host.
	 *
	 * @return the remote host
	 */
	public static final String getRemoteHost() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteHost();
	}

	/**
	 * Sets the attribute.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public static final void setAttribute(String name, String value) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		request.setAttribute(name, value);
	}

	/**
	 * Removes the attribute.
	 *
	 * @param name the name
	 */
	public static final void removeAttribute(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		request.removeAttribute(name);
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static final String getLocale() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getLocale());
	}

	/**
	 * Gets the request URI.
	 *
	 * @return the request URI
	 */
	public static final String getRequestURI() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRequestURI();
	}

	/**
	 * Checks if is secure.
	 *
	 * @return true, if is secure
	 */
	public static final boolean isSecure() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.isSecure();
	}

	/**
	 * Gets the request URL.
	 *
	 * @return the request URL
	 */
	public static final String getRequestURL() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRequestURL().toString();
	}

	/**
	 * Gets the service path.
	 *
	 * @return the service path
	 */
	public static final String getServicePath() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServletPath();
	}

	/**
	 * Gets the remote port.
	 *
	 * @return the remote port
	 */
	public static final int getRemotePort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemotePort();
	}

	/**
	 * Gets the local name.
	 *
	 * @return the local name
	 */
	public static final String getLocalName() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalName();
	}

	/**
	 * Gets the local addr.
	 *
	 * @return the local addr
	 */
	public static final String getLocalAddr() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalAddr();
	}

	/**
	 * Gets the local port.
	 *
	 * @return the local port
	 */
	public static final int getLocalPort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalPort();
	}

}
