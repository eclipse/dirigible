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
var redis = require("redis/client");
var assertTrue = require('utils/assert').assertTrue;
var redisClient = redis.getClient();

redisClient.set("name", "Alice");

var result = redisClient.get("name");

assertTrue(result !== null && result !== undefined && result == "Alice");