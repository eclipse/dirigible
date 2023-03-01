/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
exports.getContent = function () {
	return [
	  {
	    "name": "@dirigible/template",
	    "description": "Dirigible Template module",
	    "isPackageDescription": true,
	    "dtsPath": "template/extensions/template.d.ts"
	  },{
	  "name": "template/engines",
	  "description": "Template Engines API",
	  "api": "engines",
	  "versionedPaths": [
	    "template/engines"
	  ],
	  "pathDefault": "template/engines",
	  "shouldBeUnexposedToESM": true
	}
	];
};
