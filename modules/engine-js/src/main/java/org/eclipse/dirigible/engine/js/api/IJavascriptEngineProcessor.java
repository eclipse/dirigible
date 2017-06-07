package org.eclipse.dirigible.engine.js.api;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;

public interface IJavascriptEngineProcessor {

	public void executeService(String module) throws ScriptingException;

}
