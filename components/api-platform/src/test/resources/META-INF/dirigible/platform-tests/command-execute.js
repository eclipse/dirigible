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
const command = require('platform/command');
const assertTrue = require('test/assert').assertTrue;
const os = require('platform/os');

const cmdForExec = os.isWindows() ? "cmd /c echo 'hello dirigible!'" : "echo 'hello dirigible!'";
var result = command.execute(cmdForExec);
console.log("[Result]: " + result);

assertTrue(result !== undefined && result !== null);
