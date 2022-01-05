/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */

let request = __context.get('vertx.request');
let response = __context.get('vertx.response');

let message = `Hello from Eclipse Dirigible's Eclipse Vert.x server called by HTTP method ${request.method()}!`;

// write to response stream
response.write(message);

// write to system output
console.log(message);