package org.eclipse.dirigible.api.v3.http;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.eclipse.dirigible.commons.api.context.ContextException;
import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpSessionFacade implements IScriptingFacade {

	private static final String NO_VALID_REQUEST = "Trying to use HTTP Session Facade without a valid Session (HTTP Request/Response)";

	private static final Logger logger = LoggerFactory.getLogger(HttpSessionFacade.class);

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

	public static final boolean isValid() {
		HttpSession session = getSession();
		return session != null;
	}

	public static final String getAttribute(String arg0) {
		HttpSession session = getSession();
		return session.getAttribute(arg0) != null ? session.getAttribute(arg0).toString() : null;
	}

	public static final String[] getAttributeNames() {
		HttpSession session = getSession();
		return Collections.list(session.getAttributeNames()).toArray(new String[] {});
	}

	public static final long getCreationTime() {
		HttpSession session = getSession();
		return session.getCreationTime();
	}

	public static final String getId() {
		HttpSession session = getSession();
		return session.getId();
	}

	public static final long getLastAccessedTime() {
		HttpSession session = getSession();
		return session.getLastAccessedTime();
	}

	public static final int getMaxInactiveInterval() {
		HttpSession session = getSession();
		return session.getMaxInactiveInterval();
	}

	public static final void invalidate() {
		HttpSession session = getSession();
		session.invalidate();
	}

	public static final boolean isNew() {
		HttpSession session = getSession();
		return session.isNew();
	}

	public static final void setAttribute(String arg0, String arg1) {
		HttpSession session = getSession();
		session.setAttribute(arg0, arg1);
	}

	public static final void setMaxInactiveInterval(int arg0) {
		HttpSession session = getSession();
		session.setMaxInactiveInterval(arg0);
	}

}
