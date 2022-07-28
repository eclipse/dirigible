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
var httpClient = require("http/v4/client");
var response = require("http/v4/response");


let httpResponse = httpClient.get("https://www.dirigible.io/depots.json");
let depots = JSON.parse(httpResponse.text);
depots.forEach(function (depot) {
    httpResponse = httpClient.get(depot.depot);
    let plugins = JSON.parse(httpResponse.text);
    depot.plugins = plugins;
});

response.println(JSON.stringify(depots));
response.flush();
response.close();

