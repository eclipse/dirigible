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
package org.eclipse.dirigible.api.v3.http;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java facade for working with HttpSession
 */
public class HttpSessionFacade implements IScriptingFacade {

	private static final String NO_VALID_REQUEST = "Trying to use HTTP Session Facade without a valid Session (HTTP Request/Response)";
	private static final String INVOCATION_COUNT = "invocation.count";

	private static final Logger logger = LoggerFactory.getLogger(HttpSessionFacade.class);

	/**
	 * Returns the session associated with the current thread context
	 *
	 * @return the session
	 */
	static final HttpSession getSession() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			HttpServletRequest request = (HttpServletRequest) ThreadContextFacade.get(HttpServletRequest.class.getCanonicalName());
			if (request != null) {
				HttpSession httpSession = request.getSession(true);
				Integer count = (Integer)httpSession.getAttribute(INVOCATION_COUNT);
				count = count == null ? 1 : ++count;
				httpSession.setAttribute(INVOCATION_COUNT, count);
				return httpSession;
			}
		} catch (ContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Checks if there is a session associated with the current thread context
	 *
	 * @return true, if is valid
	 */
	public static final boolean isValid() {
		HttpSession session = getSession();
		return session != null;
	}

	/**
	 * Returns the object bound to the specified name
	 *
	 * @param arg0
	 *            the name
	 * @return the attribute
	 */
	public static final String getAttribute(String arg0) {
		HttpSession session = getSession();
		return session.getAttribute(arg0) != null ? session.getAttribute(arg0).toString() : null;
	}

	/**
	 * Returns the attribute names.
	 *
	 * @return the attribute names
	 */
	public static final String[] getAttributeNames() {
		HttpSession session = getSession();
		return Collections.list(session.getAttributeNames()).toArray(new String[] {});
	}
	
	/**
	 * Returns the attribute names.
	 *
	 * @return the attribute names
	 */
	public static final String getAttributeNamesJson() {
		HttpSession session = getSession();
		String[] array = Collections.list(session.getAttributeNames()).toArray(new String[] {});
		return GsonHelper.GSON.toJson(array);
	}

	/**
	 * Returns the time the session was created
	 *
	 * @return the creation time
	 */
	public static final long getCreationTime() {
		HttpSession session = getSession();
		return session.getCreationTime();
	}

	/**
	 * Returns the id of the session
	 *
	 * @return the id
	 */
	public static final String getId() {
		HttpSession session = getSession();
		return session.getId();
	}

	/**
	 * Returns the last accessed time.
	 *
	 * @return the last accessed time
	 */
	public static final long getLastAccessedTime() {
		HttpSession session = getSession();
		return session.getLastAccessedTime();
	}

	/**
	 * Returns the max inactive interval.
	 *
	 * @return the max inactive interval
	 */
	public static final int getMaxInactiveInterval() {
		HttpSession session = getSession();
		return session.getMaxInactiveInterval();
	}

	/**
	 * Invalidates the session
	 */
	public static final void invalidate() {
		HttpSession session = getSession();
		session.invalidate();
	}

	/**
	 * Checks if the session is new
	 *
	 * @return true, if is new
	 */
	public static final boolean isNew() {
		HttpSession session = getSession();
		return session.isNew();
	}

	/**
	 * Sets the attribute.
	 *
	 * @param arg0
	 *            the name
	 * @param arg1
	 *            the value
	 */
	public static final void setAttribute(String arg0, String arg1) {
		HttpSession session = getSession();
		session.setAttribute(arg0, arg1);
	}

	/**
	 * Removes the attribute.
	 *
	 * @param arg0
	 *            the name of the attribute
	 */
	public static final void removeAttribute(String arg0) {
		HttpSession session = getSession();
		session.removeAttribute(arg0);
	}

	/**
	 * Sets the max inactive interval.
	 *
	 * @param arg0
	 *            the new max inactive interval
	 */
	public static final void setMaxInactiveInterval(int arg0) {
		HttpSession session = getSession();
		session.setMaxInactiveInterval(arg0);
	}

	/**
	 * Sets the max inactive interval.
	 *
	 * @param arg0
	 *            the new max inactive interval
	 */
	public static final void setMaxInactiveInterval(Double arg0) {
		setMaxInactiveInterval(arg0.intValue());
	}

}
