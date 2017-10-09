package org.eclipse.dirigible.engine.js.processor;

import java.util.Map;
import java.util.ServiceLoader;

import org.eclipse.dirigible.commons.api.module.StaticInjector;
import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.commons.config.Configuration;
import org.eclipse.dirigible.engine.js.api.AbstractJavascriptExecutor;
import org.eclipse.dirigible.engine.js.api.IJavascriptEngineExecutor;

public class DefaultJavascriptEngineExecutor extends AbstractJavascriptExecutor implements IJavascriptEngineExecutor {

	private static final ServiceLoader<IJavascriptEngineExecutor> JAVASCRIPT_ENGINE_EXECUTORS = ServiceLoader.load(IJavascriptEngineExecutor.class);

	@Override
	public String getType() {
		return JAVASCRIPT_TYPE_DEFAULT;
	}

	@Override
	public Object executeServiceModule(String module, Map<Object, Object> executionContext) throws ScriptingException {
		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT,
				IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
		for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(javascriptEngineType)) {
				return StaticInjector.getInjector().getInstance(next.getClass()).executeServiceModule(module, executionContext);
			}
		}
		return null;
	}

	@Override
	public Object executeServiceCode(String code, Map<Object, Object> executionContext) throws ScriptingException {
		String javascriptEngineType = Configuration.get(IJavascriptEngineExecutor.DIRIGIBLE_JAVASCRIPT_TYPE_ENGINE_DEFAULT,
				IJavascriptEngineExecutor.JAVASCRIPT_TYPE_RHINO);
		for (IJavascriptEngineExecutor next : JAVASCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(javascriptEngineType)) {
				return StaticInjector.getInjector().getInstance(next.getClass()).executeServiceCode(code, executionContext);
			}
		}
		return null;
	}

}
