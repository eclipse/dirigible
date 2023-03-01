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
var url = require('utils/v4/url');
var assertTrue = require('utils/assert').assertTrue;

var input = 'http://www.test.com?var1=abc123&var2=123 456&var3=стойност';
var result = url.escape(input);
console.log(result);
assertTrue(result == 'http://www.test.com?var1=abc123&var2=123%20456&var3=%D1%81%D1%82%D0%BE%D0%B9%D0%BD%D0%BE%D1%81%D1%82');
