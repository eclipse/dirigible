package org.eclipse.dirigible.api.v3.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingContextException;
import org.eclipse.dirigible.commons.api.scripting.IScriptingFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseFacade implements IScriptingFacade {
	
	private static final Logger logger = LoggerFactory.getLogger(HttpResponseFacade.class);
	
	private static final HttpServletResponse getResponse() {
		if (!ThreadContextFacade.isValid()) {
			return null;
		}
		try {
			return (HttpServletResponse) ThreadContextFacade.get(HttpServletResponse.class.getCanonicalName());
		} catch(ScriptingContextException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static final void println(String text) {
		HttpServletResponse response = getResponse();
		if (response == null) {
			logger.error("Trying to print in an invalid response instance");
			return;
		}
		try {
			response.getOutputStream().println(text);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static boolean isCommitted() {
		HttpServletResponse response = getResponse();
		return (response != null) ? response.isCommitted() : false;
	}
    
}
