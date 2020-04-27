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
exports.getContent = function() {
	return [{
		name: "db/v4/dao",
		description: "Database DAO API"
	}, {
		name: "db/v4/database",
		description: "Database API"
	}, {
		name: "db/v4/orm",
		description: "Database ORM API"
	}, {
		name: "db/v4/ormstatements",
		description: "Database ORM Statements API"
	}, {
		name: "db/v4/query",
		description: "Database Query API"
	}, {
		name: "db/v4/sequence",
		description: "Database Sequence API"
	}, {
		name: "db/v4/sql",
		description: "SQL API"
	}, {
		name: "db/v4/update",
		description: "Database Update API"
	}];
};
