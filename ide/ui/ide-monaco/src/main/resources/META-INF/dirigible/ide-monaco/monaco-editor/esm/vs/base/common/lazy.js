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
var Lazy = /** @class */ (function () {
    function Lazy(executor) {
        this.executor = executor;
        this._didRun = false;
    }
    /**
     * Get the wrapped value.
     *
     * This will force evaluation of the lazy value if it has not been resolved yet. Lazy values are only
     * resolved once. `getValue` will re-throw exceptions that are hit while resolving the value
     */
    Lazy.prototype.getValue = function () {
        if (!this._didRun) {
            try {
                this._value = this.executor();
            }
            catch (err) {
                this._error = err;
            }
            finally {
                this._didRun = true;
            }
        }
        if (this._error) {
            throw this._error;
        }
        return this._value;
    };
    Object.defineProperty(Lazy.prototype, "rawValue", {
        /**
         * Get the wrapped value without forcing evaluation.
         */
        get: function () { return this._value; },
        enumerable: true,
        configurable: true
    });
    return Lazy;
}());
export { Lazy };
