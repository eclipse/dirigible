package org.eclipse.dirigible.components.openapi.config;

import org.eclipse.dirigible.components.base.artefact.Unit;
import org.springframework.stereotype.Component;

@Component
public class OpenAPIUnit implements Unit {

	@Override
	public String getName() {
		return "OpenAPI";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
