package org.eclipse.dirigible.components.security.config;

import org.eclipse.dirigible.components.base.artefact.Unit;
import org.springframework.stereotype.Component;

@Component
public class SecurityUnit implements Unit {

	@Override
	public String getName() {
		return "Security";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
