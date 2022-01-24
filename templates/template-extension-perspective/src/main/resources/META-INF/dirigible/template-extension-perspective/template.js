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
		"name": "Extension (Perspective)",
		"description": "Extension perspective for the IDE",
		"sources": [{
			"location": "/template-extension-perspective/extensions/menu/help.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/help.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/window.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/window.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/perspective.extension.template", 
			"action": "generate",
			"rename": "/extensions/menu/{{fileName}}.extension"
		}, {
			"location": "/template-extension-perspective/extensions/menu/menu-perspective.extensionpoint.template", 
			"action": "generate",
			"rename": "/extensions/menu/menu-{{fileName}}.extensionpoint"
		}, {
			"location": "/template-extension-perspective/extensions/perspective.extension.template", 
			"action": "generate",
			"rename": "/extensions/perspective.extension"
		}, {
			"location": "/template-extension-perspective/index.html.template", 
			"action": "generate",
			"rename": "index.html"
		}, {
			"location": "/template-extension-perspective/perspective/menu/perspective.js.template", 
			"action": "generate",
			"rename": "/perspective/menu/{{fileName}}.js"
		}, {
			"location": "/template-extension-perspective/api/menu-perspective.js.template", 
			"action": "generate",
			"rename": "/api/menu-{{fileName}}.js"
		}, {
			"location": "/template-extension-perspective/perspective/perspective.js.template", 
			"action": "generate",
			"rename": "/perspective/{{fileName}}.js"
		}],
		"parameters": []
	};
};
