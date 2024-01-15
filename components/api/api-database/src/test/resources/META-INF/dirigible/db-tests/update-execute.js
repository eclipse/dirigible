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
import { update, query } from "@dirigible/db";
import { assert } from "@dirigible/test";
var assertTrue = assert.assertTrue;

update.execute("CREATE TABLE U (A INT, B VARCHAR(10))");
update.execute("INSERT INTO U VALUES (1, 'ABC')");
update.execute("INSERT INTO U VALUES (2, 'DEF')");

var sql = "SELECT COUNT(*) AS C FROM U";
var resultset = query.execute(sql);

console.log(JSON.stringify(resultset));

update.execute("DROP TABLE U");

assertTrue(((resultset !== null) && (resultset !== undefined)));