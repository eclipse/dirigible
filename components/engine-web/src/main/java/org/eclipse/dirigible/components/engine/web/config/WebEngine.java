package org.eclipse.dirigible.components.engine.web.config;

import org.eclipse.dirigible.components.base.artefact.Engine;
import org.springframework.stereotype.Component;

@Component
public class WebEngine implements Engine {

	@Override
	public String getName() {
		return "Web";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
