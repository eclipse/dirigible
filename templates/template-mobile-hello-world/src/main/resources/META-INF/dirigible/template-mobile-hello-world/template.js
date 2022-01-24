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
		"name": "Hello World with Tabris",
		"description": "Hello World Mobile Application with Tabris",
		"sources": [
		{
			"location": "/template-mobile-hello-world/app.js.template", 
			"action": "generate",
			"rename": "{{fileName}}.js"
		},
		{
			"location": "/template-mobile-hello-world/package.json.template", 
			"action": "generate",
			"rename": "package.json"
		},
		{
			"location": "/template-mobile-hello-world/node_modules/tabris/package.json", 
			"action": "copy",
			"rename": "/node_modules/tabris/package.json"
		},
		{
			"location": "/template-mobile-hello-world/node_modules/tabris/tabris.min.js", 
			"action": "copy",
			"rename": "/node_modules/tabris/tabris.min.js"
		},
		{
			"location": "/template-mobile-hello-world/node_modules/tabris/boot.min.js", 
			"action": "copy",
			"rename": "/node_modules/tabris/boot.min.js"
		}],
		"parameters": []
	};
};
