package org.eclipse.dirigible.api.v3.http;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestFacade implements ScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestFacade.class);
	
	private static final HttpServletRequest getRequest() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			return (HttpServletRequest) ThreadContextFacade.get(HttpServletRequest.class.getCanonicalName());
		} catch(ScriptingContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static final String getMethod() {
		HttpServletRequest request = getRequest();
		return (request != null) ? request.getMethod() : null;
	}
	
	public static final String getRemoteUser() {
		HttpServletRequest request = getRequest();
		return (request != null) ? request.getRemoteUser() : null;
	}
	
}
