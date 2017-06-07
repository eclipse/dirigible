package org.eclipse.dirigible.engine.js.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.dirigible.commons.api.context.ThreadContextFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.api.service.IRestService;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;

public abstract class AbstractJavascriptEngineRestService implements IRestService {

	protected void executeService(IJavascriptEngineProcessor processor, String path, HttpServletRequest request, HttpServletResponse response) throws ScriptingException {
		ThreadContextFacade.setUp();
		try {
			ThreadContextFacade.set(HttpServletRequest.class.getCanonicalName(), request);
			ThreadContextFacade.set(HttpServletResponse.class.getCanonicalName(), response);
			processor.executeService(path);
		} finally {
			ThreadContextFacade.tearDown();
		}
	}
}
