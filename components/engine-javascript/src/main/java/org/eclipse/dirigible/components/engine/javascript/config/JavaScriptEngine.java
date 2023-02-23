package org.eclipse.dirigible.components.engine.javascript.config;

import org.eclipse.dirigible.components.base.artefact.Engine;
import org.springframework.stereotype.Component;

@Component
public class JavaScriptEngine implements Engine {

	@Override
	public String getName() {
		return "JavaScript";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
