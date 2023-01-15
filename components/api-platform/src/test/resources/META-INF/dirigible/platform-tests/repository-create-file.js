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
var repository = require("platform/repository");
var assertTrue = require('test/assert').assertTrue;

repository.createResource("/registry/public/test/file.js", "console.log('Hello World');", "application/json");
var resource = repository.getResource("/registry/public/test/file.js");
var content = resource.getText();

assertTrue(content !== undefined && content !== null);