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
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.InvalidStateException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.BytesHelper;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseFacade implements IScriptingFacade {

	private static final String NO_VALID_RESPONSE = "Trying to use HTTP Response Facade without a valid Response";

	private static final Logger logger = LoggerFactory.getLogger(HttpResponseFacade.class);

	private static final HttpServletResponse getResponse() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			return (HttpServletResponse) ThreadContextFacade.get(HttpServletResponse.class.getCanonicalName());
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public static final boolean isValid() {
		HttpServletResponse response = getResponse();
		return response != null;
	}

	public static final void print(String text) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			response.getOutputStream().print(text);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static final void print(Object o) {
		if (o != null) {
			print(o.toString());
		}
	}

	public static final void print(int i) {
		print(i + "");
	}

	public static final void print(double d) {
		print(d + "");
	}

	public static final void print(Integer i) {
		print(i + "");
	}

	public static final void print(Double d) {
		print(d + "");
	}

	public static final void println(String text) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			response.getOutputStream().println(text);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static final void println(Object o) {
		if (o != null) {
			println(o.toString());
		}
	}

	public static final void println(int i) {
		println(i + "");
	}

	public static final void println(double d) {
		println(d + "");
	}

	public static final void println(Integer i) {
		println(i + "");
	}

	public static final void println(Double d) {
		println(d + "");
	}

	public static final void write(byte[] bytes) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			response.getOutputStream().write(bytes);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static final void write(String input) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			byte[] bytes = BytesHelper.jsonToBytes(input);
			response.getOutputStream().write(bytes);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static boolean isCommitted() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.isCommitted();
	}

	public static final void setContentType(String contentType) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setContentType(contentType);
	}

	public static final void flush() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			response.getOutputStream().flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static final void close() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		try {
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static final void addCookie(String cookieJson) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		Cookie cookie = GsonHelper.GSON.fromJson(cookieJson, Cookie.class);
		response.addCookie(cookie);
	}

	public static final boolean containsHeader(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.containsHeader(name);
	}

	public static final String encodeURL(String url) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.encodeURL(url);
	}

	public static final String getCharacterEncoding() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getCharacterEncoding();
	}

	public static final String encodeRedirectURL(String url) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.encodeRedirectURL(url);
	}

	public static final String getContentType() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getContentType();
	}

	public static final void sendError(int sc, String msg) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendError(sc, msg);
	}

	public static final void sendError(Double sc, String msg) throws IOException {
		sendError(sc.intValue(), msg);
	}

	public static final void sendError(int sc) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendError(sc);
	}

	public static final void sendError(Double sc) throws IOException {
		sendError(sc.intValue());
	}

	public static final void setCharacterEncoding(String charset) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setCharacterEncoding(charset);
	}

	public static final void sendRedirect(String location) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendRedirect(location);
	}

	public static final void setContentLength(int len) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setContentLength(len);
	}

	public static final void setContentLength(Double len) {
		setContentLength(len.intValue());
	}

	public static final void setHeader(String name, String value) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setHeader(name, value);
	}

	public static final void addHeader(String name, String value) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.addHeader(name, value);
	}

	public static final void setStatus(int sc) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setStatus(sc);
	}

	public static final void setStatus(Double sc) {
		setStatus(sc.intValue());
	}

	public static final void reset() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.reset();
	}

	public static final String getHeader(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getHeader(name);
	}

	public static final void setLocale(String language) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language));
	}

	public static final void setLocale(String language, String country) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language, country));
	}

	public static final void setLocale(String language, String country, String variant) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language, country, variant));
	}

	public static final String getHeaders(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getHeaders(name).toArray());
	}

	public static final String getHeaderNames() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getHeaderNames().toArray());
	}

	public static final String getLocale() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getLocale());
	}

}
