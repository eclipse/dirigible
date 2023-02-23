package org.eclipse.dirigible.components.websockets.config;

import org.eclipse.dirigible.components.base.artefact.Unit;
import org.springframework.stereotype.Component;

@Component
public class WebSocketsUnit implements Unit {

	@Override
	public String getName() {
		return "WebSockets";
	}

	@Override
	public String getProvider() {
		return "Eclipse Dirigible";
	}

}
