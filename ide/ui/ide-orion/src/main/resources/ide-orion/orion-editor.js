/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

exports.getEditor = function() {
	var editor = {
			"id":"orion",
			"name":"Orion",
			"factory":"frame",
			"region":"center-top",
			"label":"Orion",
			"link":"../ide-orion/editor.html",
			"contentTypes":[
				"application/javascript",
				"application/json",
				"text/plain",
				"text/html",
				"application/json+extension-point",
				"application/json+extension",
				"application/json+table",
				"application/json+view",
				"application/json+job",
				"application/json+listener",
				"application/json+access",
				"application/json+roles",
				"application/xml",
				"application/bpmn+xml",
				"application/database-schema-model+xml",
				"application/entity-data-model+xml"
			]
	};
	return editor;
}
