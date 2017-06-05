package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class NashornJavascriptEngineProcessor {
	
	private static final Logger logger = LoggerFactory.getLogger(NashornJavascriptEngineProcessor.class.getCanonicalName());
	
	@Inject
	private NashornJavascriptEngineExecutor rhinoJavascriptEngineExecutor;
	
	public void executeService(String module) throws ScriptingException {
		try {
			IJavascriptEngineExecutor executor = rhinoJavascriptEngineExecutor;
			Map<Object, Object> executionContext = new HashMap<Object, Object>();
			executor.executeServiceModule(module, executionContext);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

}
