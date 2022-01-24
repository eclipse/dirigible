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
var repositoryManager = require("platform/v4/repository");
var assertTrue = require('utils/assert').assertTrue;

repositoryManager.createResource("/registry/public/test/file.js", "console.log('Hello World');", "application/json");
var resource = repositoryManager.getResource("/registry/public/test/file.js");
var content = resource.getText();

assertTrue(content !== undefined && content !== null);