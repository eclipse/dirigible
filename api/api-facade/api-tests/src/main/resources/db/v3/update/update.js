/*
 * Copyright (c) 2010-2018 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var query = require('db/v3/query');
var update = require('db/v3/update');

update.execute("CREATE TABLE T (A INT, B VARCHAR(10))");
update.execute("INSERT INTO T VALUES (1, 'ABC')");
update.execute("INSERT INTO T VALUES (2, 'DEF')");

var sql = "SELECT COUNT(*) AS C FROM T";
var resultset = query.execute(sql);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE T");

((resultset !== null) && (resultset !== undefined));