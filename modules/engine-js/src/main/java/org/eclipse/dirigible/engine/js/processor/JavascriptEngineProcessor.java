package org.eclipse.dirigible.engine.js.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.inject.Inject;

import org.apache.cxf.common.util.StringUtils;
import org.eclipse.dirigible.api.v3.http.HttpRequestFacade;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineProcessor;

public class JavascriptEngineProcessor implements IJavascriptEngineProcessor {

	private static final ServiceLoader<IJavascriptEngineExecutor> JAVASCRIPT_ENGINE_EXECUTORS = ServiceLoader.load(IJavascriptEngineExecutor.class);

	@Inject
	private IJavascriptEngineExecutor engineExecutor;

	@Override
	public void executeService(String module) throws ScriptingException {
		Map<Object, Object> executionContext = new HashMap<Object, Object>();
		getEngineExecutor().executeServiceModule(module, executionContext);
	}

	private IJavascriptEngineExecutor getEngineExecutor() {
		String headerEngineType = HttpRequestFacade.getHeader(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_ENGINE_TYPE_HEADER);
		if (!StringUtils.isEmpty(headerEngineType)) {
			for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
				if (next.getType().equals(headerEngineType)) {
					return next;
				}
			}
		}
		return engineExecutor;
	}
}
