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
var ServiceCollection = /** @class */ (function () {
    function ServiceCollection() {
        var entries = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            entries[_i] = arguments[_i];
        }
        this._entries = new Map();
        for (var _a = 0, entries_1 = entries; _a < entries_1.length; _a++) {
            var _b = entries_1[_a], id = _b[0], service = _b[1];
            this.set(id, service);
        }
    }
    ServiceCollection.prototype.set = function (id, instanceOrDescriptor) {
        var result = this._entries.get(id);
        this._entries.set(id, instanceOrDescriptor);
        return result;
    };
    ServiceCollection.prototype.has = function (id) {
        return this._entries.has(id);
    };
    ServiceCollection.prototype.get = function (id) {
        return this._entries.get(id);
    };
    return ServiceCollection;
}());
export { ServiceCollection };
