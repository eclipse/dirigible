package org.eclipse.dirigible.engine.js.v8.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;


public class V8JavascriptEngineProcessor implements IJavascriptEngineProcessor {
	
	@Inject
	private V8JavascriptEngineExecutor engineExecutor;
	
	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		engineExecutor.executeServiceModule(module, executionContext);
	}

}
