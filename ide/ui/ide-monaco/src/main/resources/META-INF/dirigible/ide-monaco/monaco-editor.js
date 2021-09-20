/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getEditor = function () {
	return {
		"id": "monaco",
		"name": "Monaco",
		"factory": "frame",
		"region": "center-top",
		"label": "Monaco",
		"link": "../ide-monaco/editor.html",
		"defaultEditor": true,
		"contentTypes": [
			"image/svg+xml",
			"text/plain",
			"text/html",
			"text/csv",
			"application/javascript",
			"application/json",
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
}