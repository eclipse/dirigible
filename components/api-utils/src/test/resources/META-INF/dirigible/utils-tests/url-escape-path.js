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
var url = require('utils/url');
var assertEquals = require('test/assert').assertEquals;

var input = 'http://www.test.com?var1=abc123&var2=123 456&var3=стойност';
var result = url.escapePath(input);
console.log(result);
assertEquals(result, 'http:%2F%2Fwww.test.com%3Fvar1=abc123&var2=123%20456&var3=%D1%81%D1%82%D0%BE%D0%B9%D0%BD%D0%BE%D1%81%D1%82');
