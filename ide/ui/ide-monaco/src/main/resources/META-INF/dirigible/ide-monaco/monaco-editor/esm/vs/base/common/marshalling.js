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
import { URI } from './uri.js';
export function parse(text) {
    var data = JSON.parse(text);
    data = revive(data);
    return data;
}
export function revive(obj, depth) {
    if (depth === void 0) { depth = 0; }
    if (!obj || depth > 200) {
        return obj;
    }
    if (typeof obj === 'object') {
        switch (obj.$mid) {
            case 1: return URI.revive(obj);
            case 2: return new RegExp(obj.source, obj.flags);
        }
        // walk object (or array)
        for (var key in obj) {
            if (Object.hasOwnProperty.call(obj, key)) {
                obj[key] = revive(obj[key], depth + 1);
            }
        }
    }
    return obj;
}
