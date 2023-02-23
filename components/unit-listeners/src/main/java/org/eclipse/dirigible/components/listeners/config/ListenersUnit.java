package org.eclipse.dirigible.components.listeners.config;

import org.eclipse.dirigible.components.base.artefact.Unit;
import org.springframework.stereotype.Component;

@Component
public class ListenersUnit implements Unit {

	@Override
	public String getName() {
		return "Listeners";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
