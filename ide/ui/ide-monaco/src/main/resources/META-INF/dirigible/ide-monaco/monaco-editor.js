/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getEditor = function () {
	var editor = {
		"id": "monaco",
		"name": "Monaco",
		"factory": "frame",
		"region": "center-top",
		"label": "Monaco",
		"link": "../ide-monaco/editor.html",
		"defaultEditor": true,
		"contentTypes": [
			"application/javascript",
			"application/json",
			"text/plain",
			"text/html",
			"text/csv",
			"application/json+extension-point",
			"application/json+extension",
			"application/json+table",
			"application/json+view",
			"application/json+job",
			"application/json+listener",
			"application/json+websocket",
			"application/json+access",
			"application/json+roles",
			"application/json+csvim",
			"application/hdbti",
			"application/xml",
			"application/bpmn+xml",
			"application/database-schema-model+xml",
			"application/entity-data-model+xml",
			"application/json+form"
		]
	};
	return editor;
}
