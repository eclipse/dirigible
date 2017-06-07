package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;


public class NashornJavascriptEngineProcessor {
	
	@Inject
	private NashornJavascriptEngineExecutor rhinoJavascriptEngineExecutor;
	
	public void executeService(String module) throws ScriptingException {
		IJavascriptEngineExecutor executor = rhinoJavascriptEngineExecutor;
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		executor.executeServiceModule(module, executionContext);
	}

}
