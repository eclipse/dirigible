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
exports.getContent = function() {
    return [
	  {
	    "name": "@dirigible/mongodb",
	    "description": "MongoDB API",
	    "isPackageDescription": true,
	    "dtsPath": "mongodb/extensions/mongodb.d.ts"
	  },
	  {
	    "name": "mongodb/client",
	    "description": "MongoDB Client API",
	    "api": "client",
	    "versionedPaths": [
	      "mongodb/client"
	    ],
	    "pathDefault": "mongodb/client"
	  },
	  {
	    "name": "mongodb/dao",
	    "description": "MongoDB Dao API",
	    "api": "dao",
	    "versionedPaths": [
	      "mongodb/dao"
	    ],
	    "pathDefault": "mongodb/dao"
	  }
	];
};