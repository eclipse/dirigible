/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
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
