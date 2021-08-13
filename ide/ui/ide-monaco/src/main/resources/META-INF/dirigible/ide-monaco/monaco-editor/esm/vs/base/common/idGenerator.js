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
var IdGenerator = /** @class */ (function () {
    function IdGenerator(prefix) {
        this._prefix = prefix;
        this._lastId = 0;
    }
    IdGenerator.prototype.nextId = function () {
        return this._prefix + (++this._lastId);
    };
    return IdGenerator;
}());
export { IdGenerator };
export var defaultGenerator = new IdGenerator('id#');
