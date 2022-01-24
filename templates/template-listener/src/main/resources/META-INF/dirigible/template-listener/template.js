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
		"name": "Message Listener",
		"description": "Listener for a message with a simple Javascript handler",
		"sources": [
		{
			"location": "/template-listener/listener.template", 
			"action": "generate",
			"rename": "{{fileName}}.listener"
		},
		{
			"location": "/template-listener/handler.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-handler.js"
		},
		{
			"location": "/template-listener/trigger.js.template", 
			"action": "generate",
			"rename": "{{fileName}}-trigger.js"
		}],
		"parameters": []
	};
};
