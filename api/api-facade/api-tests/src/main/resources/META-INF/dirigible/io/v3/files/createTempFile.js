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
var files = require('io/v3/files');
var assertTrue = require('utils/assert').assertTrue;

var tempFile = files.createTempFile("dirigible", ".txt");
console.log('Temp file: ' + tempFile);
files.writeText(tempFile, "Eclipse Dirigible");
files.deleteFile(tempFile);

assertTrue((tempFile !== null) && (tempFile !== undefined));
