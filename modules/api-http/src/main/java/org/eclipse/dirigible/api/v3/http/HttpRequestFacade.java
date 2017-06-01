package org.eclipse.dirigible.api.v3.http;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.dirigible.commons.api.scripting.ScriptingContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestFacade implements ScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestFacade.class);
	
	private static final HttpServletRequest getRequest() {
		try {
			return (HttpServletRequest) ScriptingContextFacade.get(HttpServletRequest.class.getCanonicalName());
		} catch(ScriptingContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static final String getMethod() {
		HttpServletRequest request = getRequest();
		return (request != null) ? request.getMethod() : null;
	}
    
}
