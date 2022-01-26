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
exports.getTemplate = function() {
	return {
		"name": "Websocket Service",
		"description": "Websocket service with a Javascript handler",
		"sources": [
		{
			"location": "/template-websocket/websocket.template", 
			"action": "generate",
			"rename": "{{fileName}}.websocket"
		},
		{
			"location": "/template-websocket/service-handler.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-service-handler.js"
		},
		{
			"location": "/template-websocket/client-handler.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-client-handler.js"
		},
		{
			"location": "/template-websocket/client.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-client.js"
		}],
		"parameters": []
	};
};
