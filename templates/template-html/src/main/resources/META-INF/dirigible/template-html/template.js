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
		"name": "HTML5 (AngularJS)",
		"description": "HTML5 Template with AngularJS",
		"sources": [{
			"location": "/template-html/index.html.template", 
			"action": "generate",
			"rename": "{{fileName}}.html",
			"start" : "[[",
			"end" : "]]"
		}],
		"parameters": [],
		"order": 30
	};
};
