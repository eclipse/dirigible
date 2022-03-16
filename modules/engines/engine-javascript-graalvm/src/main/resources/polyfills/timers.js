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
globalThis.setTimeout = function(fn, delay) {
    return timers.setTimeout(fn, delay);
}

globalThis.setInterval = function(fn, delay) {
    return timers.setInterval(fn, delay);
}

globalThis.clearInterval = function(timerId) {
    timers.clearInterval(timerId);
}

globalThis.clearTimeout = function(timerId) {
    timers.clearTimeout(timerId);
}

globalThis.setImmediate = function(fn) {
    timers.setImmediate(fn);
}