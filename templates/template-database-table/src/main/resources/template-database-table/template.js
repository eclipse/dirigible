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
		"name": "Database Table",
		"description": "Database Table Template",
		"sources": [{
			"location": "/template-database-table/database.table.template", 
			"action": "generate",
			"rename": "{{fileName}}.table"
		}],
		"parameters": [{
			"name": "tableName",
			"label": "Table Name"
		}]
	};
};
