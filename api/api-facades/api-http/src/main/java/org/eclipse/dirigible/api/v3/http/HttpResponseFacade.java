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

// TODO: Auto-generated Javadoc
/**
 * The Class HttpResponseFacade.
 */
public class HttpResponseFacade implements IScriptingFacade {

	/** The Constant NO_VALID_RESPONSE. */
	private static final String NO_VALID_RESPONSE = "Trying to use HTTP Response Facade without a valid Response";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpResponseFacade.class);

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
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

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public static final boolean isValid() {
		HttpServletResponse response = getResponse();
		return response != null;
	}

	/**
	 * Prints the.
	 *
	 * @param text the text
	 */
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

	/**
	 * Prints the.
	 *
	 * @param o the o
	 */
	public static final void print(Object o) {
		if (o != null) {
			print(o.toString());
		}
	}

	/**
	 * Prints the.
	 *
	 * @param i the i
	 */
	public static final void print(int i) {
		print(i + "");
	}

	/**
	 * Prints the.
	 *
	 * @param d the d
	 */
	public static final void print(double d) {
		print(d + "");
	}

	/**
	 * Prints the.
	 *
	 * @param i the i
	 */
	public static final void print(Integer i) {
		print(i + "");
	}

	/**
	 * Prints the.
	 *
	 * @param d the d
	 */
	public static final void print(Double d) {
		print(d + "");
	}

	/**
	 * Println.
	 *
	 * @param text the text
	 */
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

	/**
	 * Println.
	 *
	 * @param o the o
	 */
	public static final void println(Object o) {
		if (o != null) {
			println(o.toString());
		}
	}

	/**
	 * Println.
	 *
	 * @param i the i
	 */
	public static final void println(int i) {
		println(i + "");
	}

	/**
	 * Println.
	 *
	 * @param d the d
	 */
	public static final void println(double d) {
		println(d + "");
	}

	/**
	 * Println.
	 *
	 * @param i the i
	 */
	public static final void println(Integer i) {
		println(i + "");
	}

	/**
	 * Println.
	 *
	 * @param d the d
	 */
	public static final void println(Double d) {
		println(d + "");
	}

	/**
	 * Write.
	 *
	 * @param bytes the bytes
	 */
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

	/**
	 * Write.
	 *
	 * @param input the input
	 */
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

	/**
	 * Checks if is committed.
	 *
	 * @return true, if is committed
	 */
	public static boolean isCommitted() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.isCommitted();
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType the new content type
	 */
	public static final void setContentType(String contentType) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setContentType(contentType);
	}

	/**
	 * Flush.
	 */
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

	/**
	 * Close.
	 */
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

	/**
	 * Adds the cookie.
	 *
	 * @param cookieJson the cookie json
	 */
	public static final void addCookie(String cookieJson) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		Cookie cookie = GsonHelper.GSON.fromJson(cookieJson, Cookie.class);
		response.addCookie(cookie);
	}

	/**
	 * Contains header.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public static final boolean containsHeader(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.containsHeader(name);
	}

	/**
	 * Encode URL.
	 *
	 * @param url the url
	 * @return the string
	 */
	public static final String encodeURL(String url) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.encodeURL(url);
	}

	/**
	 * Gets the character encoding.
	 *
	 * @return the character encoding
	 */
	public static final String getCharacterEncoding() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getCharacterEncoding();
	}

	/**
	 * Encode redirect URL.
	 *
	 * @param url the url
	 * @return the string
	 */
	public static final String encodeRedirectURL(String url) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.encodeRedirectURL(url);
	}

	/**
	 * Gets the content type.
	 *
	 * @return the content type
	 */
	public static final String getContentType() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getContentType();
	}

	/**
	 * Send error.
	 *
	 * @param sc the sc
	 * @param msg the msg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void sendError(int sc, String msg) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendError(sc, msg);
	}

	/**
	 * Send error.
	 *
	 * @param sc the sc
	 * @param msg the msg
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void sendError(Double sc, String msg) throws IOException {
		sendError(sc.intValue(), msg);
	}

	/**
	 * Send error.
	 *
	 * @param sc the sc
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void sendError(int sc) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendError(sc);
	}

	/**
	 * Send error.
	 *
	 * @param sc the sc
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void sendError(Double sc) throws IOException {
		sendError(sc.intValue());
	}

	/**
	 * Sets the character encoding.
	 *
	 * @param charset the new character encoding
	 */
	public static final void setCharacterEncoding(String charset) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setCharacterEncoding(charset);
	}

	/**
	 * Send redirect.
	 *
	 * @param location the location
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static final void sendRedirect(String location) throws IOException {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.sendRedirect(location);
	}

	/**
	 * Sets the content length.
	 *
	 * @param len the new content length
	 */
	public static final void setContentLength(int len) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setContentLength(len);
	}

	/**
	 * Sets the content length.
	 *
	 * @param len the new content length
	 */
	public static final void setContentLength(Double len) {
		setContentLength(len.intValue());
	}

	/**
	 * Sets the header.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public static final void setHeader(String name, String value) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setHeader(name, value);
	}

	/**
	 * Adds the header.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public static final void addHeader(String name, String value) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.addHeader(name, value);
	}

	/**
	 * Sets the status.
	 *
	 * @param sc the new status
	 */
	public static final void setStatus(int sc) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setStatus(sc);
	}

	/**
	 * Sets the status.
	 *
	 * @param sc the new status
	 */
	public static final void setStatus(Double sc) {
		setStatus(sc.intValue());
	}

	/**
	 * Reset.
	 */
	public static final void reset() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.reset();
	}

	/**
	 * Gets the header.
	 *
	 * @param name the name
	 * @return the header
	 */
	public static final String getHeader(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return response.getHeader(name);
	}

	/**
	 * Sets the locale.
	 *
	 * @param language the new locale
	 */
	public static final void setLocale(String language) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language));
	}

	/**
	 * Sets the locale.
	 *
	 * @param language the language
	 * @param country the country
	 */
	public static final void setLocale(String language, String country) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language, country));
	}

	/**
	 * Sets the locale.
	 *
	 * @param language the language
	 * @param country the country
	 * @param variant the variant
	 */
	public static final void setLocale(String language, String country, String variant) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		response.setLocale(new Locale(language, country, variant));
	}

	/**
	 * Gets the headers.
	 *
	 * @param name the name
	 * @return the headers
	 */
	public static final String getHeaders(String name) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getHeaders(name).toArray());
	}

	/**
	 * Gets the header names.
	 *
	 * @return the header names
	 */
	public static final String getHeaderNames() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getHeaderNames().toArray());
	}

	/**
	 * Gets the locale.
	 *
	 * @return the locale
	 */
	public static final String getLocale() {
		HttpServletResponse response = getResponse();
		if (response == null) {
			throw new InvalidStateException(NO_VALID_RESPONSE);
		}
		return GsonHelper.GSON.toJson(response.getLocale());
	}

}
