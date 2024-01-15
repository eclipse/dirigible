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
import { update, database } from "@dirigible/db";
import { assert } from "@dirigible/test";
var assertTrue = assert.assertTrue;

update.execute("CREATE TABLE T (A INT, B VARCHAR(10))");
update.execute("INSERT INTO T VALUES (1, 'ABC')");
update.execute("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT * FROM T WHERE A = ?";

var value;
var connection = database.getConnection();
try {
	var statement = connection.prepareStatement(sql);
	try {
		statement.setInt(1, 2);
		var resultset = statement.executeQuery();
		try {
			while (resultset.next()) {
				var value = resultset.getString('B');
				console.log('B: ' + value);
			}
		} finally {
			resultset.close();
		}
	} finally {
		statement.close();
	}
} finally {
	connection.close();
}

update.execute("DROP TABLE T");

assertTrue(value == 'DEF');