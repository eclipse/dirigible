/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var database = require('db/v4/database');
var update = require('db/v4/update');

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

value == 'DEF';