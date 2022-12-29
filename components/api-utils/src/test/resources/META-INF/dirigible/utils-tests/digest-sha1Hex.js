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
var digest = require('utils/digest');
var assertEquals = require('test/assert').assertEquals;

var input = [61, 62, 63];
var result = digest.sha1Hex(input);

console.log(result);

assertEquals(result, '3b543c8b5ddc61fe39de1e5a3aece34082b12777');
