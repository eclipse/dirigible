/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var cassandra = require("cassandra/client");

var cassandraSession = cassandra.getSession("127.0.0.1", 9042);

cassandraSession.execute("CREATE KEYSPACE IF NOT EXISTS test WITH replication ={'class':'SimpleStrategy','replication_factor':'1'};");

java.lang.Thread.sleep(500);
cassandraSession.execute("use test");
java.lang.Thread.sleep(500);
cassandraSession.execute("create table if not exist test_table(id int primary key,name varchar,age int)");
java.lang.Thread.sleep(500);
cassandraSession.execute("insert into test_table(id,name,age) values (1,'test_user',18)");
java.lang.Thread.sleep(500);

var resultSet = cassandraSession.getDBResults(cassandraSession,"test", "select*from test_table");
var stringResult = resultSet.getRowAsString();

stringResult !== null && stringResult !== undefined && stringResult === "[Row[1, test_user, 18]]";


