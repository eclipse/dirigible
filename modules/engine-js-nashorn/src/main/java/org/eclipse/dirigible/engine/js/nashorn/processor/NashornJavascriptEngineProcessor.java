package org.eclipse.dirigible.engine.js.nashorn.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;


public class NashornJavascriptEngineProcessor implements IJavascriptEngineProcessor{
	
	@Inject
	private NashornJavascriptEngineExecutor nashornEngineExecutor;

	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		nashornEngineExecutor.executeServiceModule(module, executionContext);
	}

}
