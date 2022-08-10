/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.dirigible.runtime.websockets.service;

import java.util.List;
import java.util.Map;

import org.eclipse.dirigible.commons.api.scripting.ScriptingException;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;
import org.eclipse.dirigible.engine.api.script.ScriptEngineExecutorsManager;

/**
 * The Class WebsocketHandler.
 */
public class WebsocketHandler {
	
	/** The websockets core service. */
	private WebsocketsCoreService websocketsCoreService = new WebsocketsCoreService();
	
	/**
	 * Process the event.
	 *
	 * @param endpoint the endpoint
	 * @param wrapper the wrapper
	 * @param context the context
	 * @throws WebsocketsException the websockets exception
	 */
	public void processEvent(String endpoint, String wrapper, Map<Object, Object> context) throws WebsocketsException {
		List<WebsocketDefinition> websocketByEndpointList = websocketsCoreService.getWebsocketByEndpoint(endpoint);
		if (websocketByEndpointList.size() > 0) {
			WebsocketDefinition websocketDefinition = websocketByEndpointList.get(0);
			String module = websocketDefinition.getHandler();
			String engine = websocketDefinition.getEngine();
			try {
				if (engine == null) {
					engine = "javascript";
				}
				context.put("handler", module);
				ScriptEngineExecutorsManager.executeServiceModule(engine, wrapper, context);
			} catch (ScriptingException e) {
				throw new WebsocketsException(e);
			}
		}
	}

}
