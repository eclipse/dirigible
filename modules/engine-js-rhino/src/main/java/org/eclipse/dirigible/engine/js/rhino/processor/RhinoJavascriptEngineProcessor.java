package org.eclipse.dirigible.engine.js.rhino.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;


public class RhinoJavascriptEngineProcessor implements IJavascriptEngineProcessor {
	
	@Inject
	private RhinoJavascriptEngineExecutor engineExecutor;

	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		engineExecutor.executeServiceModule(module, executionContext);
	}

}
