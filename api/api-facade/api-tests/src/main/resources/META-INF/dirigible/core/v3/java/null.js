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
var _java = require('core/v3/java');

var arrayList = _java.instantiate('java.util.ArrayList', []);

_java.invoke(arrayList.uuid, 'add', [null]);

var result = _java.invoke(arrayList.uuid, 'get', [0]);

console.log(result);

result == null;
