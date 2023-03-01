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
var base64 = require('utils/v4/base64');
var assertTrue = require('utils/assert').assertTrue;

var input = 'PT4/';
var result = base64.decode(input);

console.log('decoded: ' + result);

assertTrue(result[0] === 61 && result[1] === 62 && result[2] === 63);
