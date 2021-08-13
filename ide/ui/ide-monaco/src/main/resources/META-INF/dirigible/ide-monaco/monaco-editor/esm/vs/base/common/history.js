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
import { ArrayNavigator } from './iterator.js';
var HistoryNavigator = /** @class */ (function () {
    function HistoryNavigator(history, limit) {
        if (history === void 0) { history = []; }
        if (limit === void 0) { limit = 10; }
        this._initialize(history);
        this._limit = limit;
        this._onChange();
    }
    HistoryNavigator.prototype.add = function (t) {
        this._history.delete(t);
        this._history.add(t);
        this._onChange();
    };
    HistoryNavigator.prototype.next = function () {
        return this._navigator.next();
    };
    HistoryNavigator.prototype.previous = function () {
        return this._navigator.previous();
    };
    HistoryNavigator.prototype.current = function () {
        return this._navigator.current();
    };
    HistoryNavigator.prototype.parent = function () {
        return null;
    };
    HistoryNavigator.prototype.first = function () {
        return this._navigator.first();
    };
    HistoryNavigator.prototype.last = function () {
        return this._navigator.last();
    };
    HistoryNavigator.prototype.has = function (t) {
        return this._history.has(t);
    };
    HistoryNavigator.prototype._onChange = function () {
        this._reduceToLimit();
        var elements = this._elements;
        this._navigator = new ArrayNavigator(elements, 0, elements.length, elements.length);
    };
    HistoryNavigator.prototype._reduceToLimit = function () {
        var data = this._elements;
        if (data.length > this._limit) {
            this._initialize(data.slice(data.length - this._limit));
        }
    };
    HistoryNavigator.prototype._initialize = function (history) {
        this._history = new Set();
        for (var _i = 0, history_1 = history; _i < history_1.length; _i++) {
            var entry = history_1[_i];
            this._history.add(entry);
        }
    };
    Object.defineProperty(HistoryNavigator.prototype, "_elements", {
        get: function () {
            var elements = [];
            this._history.forEach(function (e) { return elements.push(e); });
            return elements;
        },
        enumerable: true,
        configurable: true
    });
    return HistoryNavigator;
}());
export { HistoryNavigator };
