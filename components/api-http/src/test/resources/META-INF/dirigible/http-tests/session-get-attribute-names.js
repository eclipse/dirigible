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
var session = require('http/session');
var assertEquals = require('test/assert').assertEquals;

session.setAttribute('attr1', 'value1');

assertEquals(JSON.stringify(session.getAttributeNames()), '["SPRING_SECURITY_CONTEXT","invocation.count","attr1"]');

