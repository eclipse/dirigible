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
var digest = require('utils/v4/digest');
var assertTrue = require('utils/assert').assertTrue;

var input = [41, 42, 43];
var result = digest.sha1(input);

console.log(JSON.stringify(result));

assertTrue(result.length === 20 && result[0] === 77);
