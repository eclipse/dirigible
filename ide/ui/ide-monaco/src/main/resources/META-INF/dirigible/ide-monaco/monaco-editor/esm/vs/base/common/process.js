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
import { isWindows, isMacintosh, setImmediate } from './platform.js';
var safeProcess = (typeof process === 'undefined') ? {
    cwd: function () { return '/'; },
    env: Object.create(null),
    get platform() { return isWindows ? 'win32' : isMacintosh ? 'darwin' : 'linux'; },
    nextTick: function (callback) { return setImmediate(callback); }
} : process;
export var cwd = safeProcess.cwd;
export var env = safeProcess.env;
export var platform = safeProcess.platform;
