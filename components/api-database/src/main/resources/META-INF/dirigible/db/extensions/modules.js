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
		"name": "@dirigible/db",
	    "description": "Dirigible DB module",
	    "isPackageDescription": true,
	    "dtsPath": "db/extensions/db.d.ts"
	  },
	  {
	    "name": "db/dao",
	    "description": "Database DAO API",
	    "api": "dao",
	    "versionedPaths": [
	      "db/dao"
	    ],
	    "pathDefault": "db/dao"
	  },
	  {
	    "name": "db/database",
	    "description": "Database API",
	    "api": "database",
	    "versionedPaths": [
	      "db/database"
	    ],
	    "pathDefault": "db/database"
	  },
	  {
	    "name": "db/orm",
	    "description": "Database ORM API",
	    "api": "orm",
	    "versionedPaths": [
	      "db/orm"
	    ],
	    "pathDefault": "db/orm"
	  },
	  {
	    "name": "db/ormstatements",
	    "description": "Database ORM Statements API",
	    "api": "ormstatements",
	    "versionedPaths": [
	      "db/ormstatements"
	    ],
	    "pathDefault": "db/ormstatements"
	  },
	  {
	    "name": "db/procedure",
	    "description": "Database Procedure API",
	    "api": "procedure",
	    "versionedPaths": [
	      "db/procedure"
	    ],
	    "pathDefault": "db/procedure"
	  },
	  {
	    "name": "db/query",
	    "description": "Database Query API",
	    "api": "query",
	    "versionedPaths": [
	      "db/query"
	    ],
	    "pathDefault": "db/query"
	  },
	  {
	    "name": "db/sequence",
	    "description": "Database Sequence API",
	    "api": "sequence",
	    "versionedPaths": [
	      "db/sequence"
	    ],
	    "pathDefault": "db/sequence"
	  },
	  {
	    "name": "db/sql",
	    "description": "SQL API",
	    "api": "sql",
	    "versionedPaths": [
	      "db/sql"
	    ],
	    "pathDefault": "db/sql"
	  },
	  {
	    "name": "db/update",
	    "description": "Database Update API",
	    "api": "update",
	    "versionedPaths": [
	      "db/update"
	    ],
	    "pathDefault": "db/update"
	  }
	];

};
