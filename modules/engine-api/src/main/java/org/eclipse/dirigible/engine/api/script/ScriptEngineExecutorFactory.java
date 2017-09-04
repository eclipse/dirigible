package org.eclipse.dirigible.engine.api.script;

import static java.text.MessageFormat.format;

import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptEngineExecutorFactory {

	private static final ServiceLoader<IScriptEngineExecutor> SCRIPT_ENGINE_EXECUTORS = ServiceLoader.load(IScriptEngineExecutor.class);

	private static final Logger logger = LoggerFactory.getLogger(ScriptEngineExecutorFactory.class);

	public static IScriptEngineExecutor getScriptEngineExecutor(String type) {
		for (IScriptEngineExecutor next : SCRIPT_ENGINE_EXECUTORS) {
			if (next.getType().equals(type)) {
				return next;
			}
		}
		logger.error(format("Script Executor of Type {0} does not exist.", type));
		return null;
	}

}
