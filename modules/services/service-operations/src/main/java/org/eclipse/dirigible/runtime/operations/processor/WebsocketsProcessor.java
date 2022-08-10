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
package org.eclipse.dirigible.runtime.operations.processor;

import java.util.List;

import org.eclipse.dirigible.commons.api.helpers.GsonHelper;
import org.eclipse.dirigible.core.websockets.api.WebsocketsException;
import org.eclipse.dirigible.core.websockets.definition.WebsocketDefinition;
import org.eclipse.dirigible.core.websockets.service.WebsocketsCoreService;

/**
 * The Class WebsocketsProcessor.
 */
public class WebsocketsProcessor {
	
	/** The websockets core service. */
	private WebsocketsCoreService websocketsCoreService = new WebsocketsCoreService();
	
	/**
	 * List.
	 *
	 * @return the string
	 * @throws WebsocketsException the websockets exception
	 */
	public String list() throws WebsocketsException {
		
		List<WebsocketDefinition> websockets = websocketsCoreService.getWebsockets();
		
        return GsonHelper.GSON.toJson(websockets);
	}


}
