package org.eclipse.dirigible.api.v3.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.scripting.ScriptingContextException;
import org.eclipse.dirigible.commons.api.scripting.ScriptingContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseFacade implements ScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpResponseFacade.class);
	
	private static final HttpServletResponse getResponse() {
		try {
			return (HttpServletResponse) ScriptingContextFacade.get(HttpServletResponse.class.getCanonicalName());
		} catch(ScriptingContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static final boolean println(String text) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			return false;
		}
		try {
			response.getWriter().println(text);
			return true;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
    
}
