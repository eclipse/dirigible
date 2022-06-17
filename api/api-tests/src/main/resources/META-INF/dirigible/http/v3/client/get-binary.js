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
var client = require('http/v3/client');
var assertTrue = require('utils/assert').assertTrue;

var result = client.get('https://raw.githubusercontent.com/eclipse/dirigible/master/NOTICE.txt', {'binary': true});

console.log(JSON.stringify(result));

assertTrue((result !== null) && (result !== undefined));
