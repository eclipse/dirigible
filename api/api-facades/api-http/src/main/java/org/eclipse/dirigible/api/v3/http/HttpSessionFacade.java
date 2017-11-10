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

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpSessionFacade.
 */
public class HttpSessionFacade implements IScriptingFacade {

	/** The Constant NO_VALID_REQUEST. */
	private static final String NO_VALID_REQUEST = "Trying to use HTTP Session Facade without a valid Session (HTTP Request/Response)";

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpSessionFacade.class);

	/**
	 * Gets the session.
	 *
	 * @return the session
	 */
	static final HttpSession getSession() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			HttpServletRequest request = (HttpServletRequest) ThreadContextFacade.get(HttpServletRequest.class.getCanonicalName());
			return request.getSession(true);
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
		HttpSession session = getSession();
		return session != null;
	}

	/**
	 * Gets the attribute.
	 *
	 * @param arg0 the arg 0
	 * @return the attribute
	 */
	public static final String getAttribute(String arg0) {
		HttpSession session = getSession();
		return session.getAttribute(arg0) != null ? session.getAttribute(arg0).toString() : null;
	}

	/**
	 * Gets the attribute names.
	 *
	 * @return the attribute names
	 */
	public static final String[] getAttributeNames() {
		HttpSession session = getSession();
		return Collections.list(session.getAttributeNames()).toArray(new String[] {});
	}

	/**
	 * Gets the creation time.
	 *
	 * @return the creation time
	 */
	public static final long getCreationTime() {
		HttpSession session = getSession();
		return session.getCreationTime();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public static final String getId() {
		HttpSession session = getSession();
		return session.getId();
	}

	/**
	 * Gets the last accessed time.
	 *
	 * @return the last accessed time
	 */
	public static final long getLastAccessedTime() {
		HttpSession session = getSession();
		return session.getLastAccessedTime();
	}

	/**
	 * Gets the max inactive interval.
	 *
	 * @return the max inactive interval
	 */
	public static final int getMaxInactiveInterval() {
		HttpSession session = getSession();
		return session.getMaxInactiveInterval();
	}

	/**
	 * Invalidate.
	 */
	public static final void invalidate() {
		HttpSession session = getSession();
		session.invalidate();
	}

	/**
	 * Checks if is new.
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
	 * @param arg0 the arg 0
	 * @param arg1 the arg 1
	 */
	public static final void setAttribute(String arg0, String arg1) {
		HttpSession session = getSession();
		session.setAttribute(arg0, arg1);
	}

	/**
	 * Removes the attribute.
	 *
	 * @param arg0 the arg 0
	 */
	public static final void removeAttribute(String arg0) {
		HttpSession session = getSession();
		session.removeAttribute(arg0);
	}

	/**
	 * Sets the max inactive interval.
	 *
	 * @param arg0 the new max inactive interval
	 */
	public static final void setMaxInactiveInterval(int arg0) {
		HttpSession session = getSession();
		session.setMaxInactiveInterval(arg0);
	}

	/**
	 * Sets the max inactive interval.
	 *
	 * @param arg0 the new max inactive interval
	 */
	public static final void setMaxInactiveInterval(Double arg0) {
		setMaxInactiveInterval(arg0.intValue());
	}

}
