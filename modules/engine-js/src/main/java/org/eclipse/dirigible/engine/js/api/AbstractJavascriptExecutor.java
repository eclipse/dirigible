package org.eclipse.dirigible.engine.js.api;

import org.eclipse.dirigible.api.v3.http.HttpResponseFacade;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;

public abstract class AbstractJavascriptExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

	public static final String MODULE_EXT_JS = ".js/";
	public static final String MODULE_EXT_RHINO = ".rhino/";
	public static final String MODULE_EXT_NASHORN = ".nashorn/";
	public static final String MODULE_EXT_V8 = ".v8/";

	protected void forceFlush() {
		try {
			if (HttpResponseFacade.isValid()) {
				HttpResponseFacade.flush();
				HttpResponseFacade.close();
			}
		} catch (Exception e) {
			// no need to log it
		}
	}

	protected String trimPathParameters(String module, String moduleExtension) {
		if (module.indexOf(moduleExtension) > 0) {
			module = module.substring(0, module.indexOf(moduleExtension) + 3);
		}
		return module;
	}

}
