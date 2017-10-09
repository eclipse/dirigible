package org.eclipse.dirigible.engine.api.script;

import static java.text.MessageFormat.format;

import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

public class ScriptEngineExecutorsManager {

	public static Object executeServiceModule(String engineType, String module, Map<Object, Object> executionContext) throws ScriptingException {
		IScriptEngineExecutor scriptEngineExecutor = ScriptEngineExecutorFactory.getScriptEngineExecutor(engineType);
		if (scriptEngineExecutor != null) {
			return scriptEngineExecutor.executeServiceModule(module, executionContext);
		}

		throw new ScriptingException(
				format("Script Executor of Type {0} does not exist, hence the Module{1} cannot be processed", engineType, module));
	}

	public static Object executeServiceCode(String engineType, String module, Map<Object, Object> executionContext) throws ScriptingException {
		IScriptEngineExecutor scriptEngineExecutor = ScriptEngineExecutorFactory.getScriptEngineExecutor(engineType);
		if (scriptEngineExecutor != null) {
			return scriptEngineExecutor.executeServiceCode(module, executionContext);
		}

		throw new ScriptingException(
				format("Script Executor of Type {0} does not exist, hence the Module{1} cannot be processed", engineType, module));
	}

}
