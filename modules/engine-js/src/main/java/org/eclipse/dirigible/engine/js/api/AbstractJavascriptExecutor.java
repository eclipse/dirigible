package org.eclipse.dirigible.engine.js.api;

import org.eclipse.dirigible.api.v3.http.HttpResponseFacade;
import org.eclipse.dirigible.engine.api.script.AbstractScriptExecutor;

public abstract class AbstractJavascriptExecutor extends AbstractScriptExecutor implements IJavascriptEngineExecutor {

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

}
