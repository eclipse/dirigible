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
var spark = require("spark/client");
var assertTrue = require('utils/assert').assertTrue;
var sparkSession = spark.getSession("spark://192.168.0.143:7077");
var dataset = sparkSession.readFormat("src/test/resources/data/UserData.csv", "csv");
var result = dataset.getRowAsString(0)

assertTrue(result === "[id;name;familyname;personalNumber;age]");