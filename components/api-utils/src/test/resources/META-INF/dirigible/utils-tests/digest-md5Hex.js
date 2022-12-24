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
var assertEquals = require('utils/assert').assertEquals;

var input = 'ABC';
var result = digest.md5Hex(input);

console.log(result);

assertEquals(result, '902fbdd2b1df0c4f70b4a5d23525e932');
