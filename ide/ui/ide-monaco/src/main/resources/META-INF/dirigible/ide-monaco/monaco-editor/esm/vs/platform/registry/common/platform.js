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
import * as Types from '../../../base/common/types.js';
import * as Assert from '../../../base/common/assert.js';
var RegistryImpl = /** @class */ (function () {
    function RegistryImpl() {
        this.data = new Map();
    }
    RegistryImpl.prototype.add = function (id, data) {
        Assert.ok(Types.isString(id));
        Assert.ok(Types.isObject(data));
        Assert.ok(!this.data.has(id), 'There is already an extension with this id');
        this.data.set(id, data);
    };
    RegistryImpl.prototype.as = function (id) {
        return this.data.get(id) || null;
    };
    return RegistryImpl;
}());
export var Registry = new RegistryImpl();
