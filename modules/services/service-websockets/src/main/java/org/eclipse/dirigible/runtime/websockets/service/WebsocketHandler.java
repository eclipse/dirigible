/**
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
package org.eclipse.dirigible.runtime.websockets.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;

public class WebsocketHandler {
	
	@Inject
	private WebsocketsCoreService websocketsCoreService;
	
	/**
	 * Process the event
	 * 
	 * @param endpoint the endpoint
	 * @param context the context
	 * @throws WebsocketsException
	 */
	public void processEvent(String endpoint, Map<Object, Object> context) throws WebsocketsException {
		List<WebsocketDefinition> websocketByEndpointList = websocketsCoreService.getWebsocketByEndpoint(endpoint);
		if (websocketByEndpointList.size() > 0) {
			WebsocketDefinition websocketDefinition = websocketByEndpointList.get(0);
			String module = websocketDefinition.getHandler();
			String engine = websocketDefinition.getEngine();
			try {
				if (engine == null) {
					engine = "javascript";
				}
				ScriptEngineExecutorsManager.executeServiceModule(engine, module, context);
			} catch (ScriptingException e) {
				throw new WebsocketsException(e);
			}
		}
	}

}
