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

public class HttpRequestFacade implements IScriptingFacade {

	private static final String NO_VALID_REQUEST = "Trying to use HTTP Request Facade without a valid Request";

	private static final Logger logger = LoggerFactory.getLogger(HttpRequestFacade.class);

	private static final HttpServletRequest getRequest() {
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

	public static final boolean isValid() {
		HttpServletRequest request = getRequest();
		return request != null;
	}

	public static final String getMethod() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getMethod();
	}

	public static final String getRemoteUser() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteUser();
	}

	public static final String getPathInfo() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getPathInfo();
	}

	public static final String getPathTranslated() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getPathTranslated();
	}

	public static final String getHeader(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getHeader(name);
	}

	public static final boolean isUserInRole(String role) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.isUserInRole(role);
	}

	public static final String getAttribute(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getAttribute(name) != null ? request.getAttribute(name).toString() : null;
	}

	public static final String getAuthType() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getAuthType();
	}

	public static final String getCookies() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getCookies());
	}

	public static final String getAttributeNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getAttributeNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	public static final String getCharacterEncoding() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getCharacterEncoding();
	}

	public static final int getContentLength() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContentLength();
	}

	public static final String getHeaders(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getHeaders(name));
		return GsonHelper.GSON.toJson(list.toArray());
	}

	public static final String getContentType() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContentType();
	}

	public static final String getBytes() throws IOException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return BytesHelper.bytesToJson(IOUtils.toByteArray(request.getInputStream()));
	}

	public static final String getText() throws IOException {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		byte[] bytes = IOUtils.toByteArray(request.getInputStream());
		String charset = (request.getCharacterEncoding() != null) ? request.getCharacterEncoding() : StandardCharsets.UTF_8.name();
		return new String(bytes, charset);
	}

	public static final String getParameter(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getParameter(name);
	}

	public static final String getHeaderNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getHeaderNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	public static final String getParameterNames() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		List<String> list = Collections.list(request.getParameterNames());
		return GsonHelper.GSON.toJson(list.toArray());
	}

	public static final String getParameterValues(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getParameterValues(name));
	}

	public static final String getProtocol() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getProtocol();
	}

	public static final String getScheme() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getScheme();
	}

	public static final String getContextPath() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getContextPath();
	}

	public static final String getServerName() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServerName();
	}

	public static final int getServerPort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServerPort();
	}

	public static final String getQueryString() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getQueryString();
	}

	public static final String getRemoteAddress() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteAddr();
	}

	public static final String getRemoteHost() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemoteHost();
	}

	public static final void setAttribute(String name, String value) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		request.setAttribute(name, value);
	}

	public static final void removeAttribute(String name) {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		request.removeAttribute(name);
	}

	public static final String getLocale() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return GsonHelper.GSON.toJson(request.getLocale());
	}

	public static final String getRequestURI() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRequestURI();
	}

	public static final boolean isSecure() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.isSecure();
	}

	public static final String getRequestURL() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRequestURL().toString();
	}

	public static final String getServicePath() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getServletPath();
	}

	public static final int getRemotePort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getRemotePort();
	}

	public static final String getLocalName() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalName();
	}

	public static final String getLocalAddr() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalAddr();
	}

	public static final int getLocalPort() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			throw new InvalidStateException(NO_VALID_REQUEST);
		}
		return request.getLocalPort();
	}

}
