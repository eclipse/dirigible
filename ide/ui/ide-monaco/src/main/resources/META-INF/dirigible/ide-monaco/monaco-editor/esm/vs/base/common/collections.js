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
var hasOwnProperty = Object.prototype.hasOwnProperty;
/**
 * Returns an array which contains all values that reside
 * in the given set.
 */
export function values(from) {
    var result = [];
    for (var key in from) {
        if (hasOwnProperty.call(from, key)) {
            result.push(from[key]);
        }
    }
    return result;
}
export function first(from) {
    for (var key in from) {
        if (hasOwnProperty.call(from, key)) {
            return from[key];
        }
    }
    return undefined;
}
/**
 * Iterates over each entry in the provided set. The iterator allows
 * to remove elements and will stop when the callback returns {{false}}.
 */
export function forEach(from, callback) {
    var _loop_1 = function (key) {
        if (hasOwnProperty.call(from, key)) {
            var result = callback({ key: key, value: from[key] }, function () {
                delete from[key];
            });
            if (result === false) {
                return { value: void 0 };
            }
        }
    };
    for (var key in from) {
        var state_1 = _loop_1(key);
        if (typeof state_1 === "object")
            return state_1.value;
    }
}
var SetMap = /** @class */ (function () {
    function SetMap() {
        this.map = new Map();
    }
    SetMap.prototype.add = function (key, value) {
        var values = this.map.get(key);
        if (!values) {
            values = new Set();
            this.map.set(key, values);
        }
        values.add(value);
    };
    SetMap.prototype.delete = function (key, value) {
        var values = this.map.get(key);
        if (!values) {
            return;
        }
        values.delete(value);
        if (values.size === 0) {
            this.map.delete(key);
        }
    };
    SetMap.prototype.forEach = function (key, fn) {
        var values = this.map.get(key);
        if (!values) {
            return;
        }
        values.forEach(fn);
    };
    return SetMap;
}());
export { SetMap };
