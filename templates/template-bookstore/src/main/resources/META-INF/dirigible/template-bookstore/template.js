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
		"name": "Bookstore Application",
		"description": "Bookstore Application Sample with Table, REST Service and UI (AngularJS)",
		"sources": [
		{
			"location": "/template-bookstore/data/bookstore.table.template", 
			"action": "generate",
			"rename": "data/{{fileName}}.table"
		},
		{
			"location": "/template-bookstore/dao/bookstore.js.template", 
			"action": "generate",
			"rename": "dao/{{fileName}}.js"
		},
		{
			"location": "/template-bookstore/service/bookstore.js.template", 
			"action": "generate",
			"rename": "service/{{fileName}}.js"
		},
		{
			"location": "/template-bookstore/view/index.html.template", 
			"action": "generate",
			"rename": "view/{{fileName}}.html",
			"start" : "[[",
			"end" : "]]"
		},
		{
			"location": "/template-bookstore/view/controller.js.template", 
			"action": "generate",
			"rename": "view/{{fileName}}.js"
		}],
		"parameters": [],
		"order": 50
	};
};
