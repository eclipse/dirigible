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
var xml = require('utils/v3/xml');
var assertTrue = require('utils/assert').assertTrue;

var input = '{"a":{"b":"text_b","c":"text_c","d":{"e":"text_e"}}}';
var result = xml.fromJson(input);

assertTrue(result === '<a><b>text_b</b><c>text_c</c><d><e>text_e</e></d></a>');
