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
import { Emitter } from '../../../base/common/event.js';
import { toDisposable } from '../../../base/common/lifecycle.js';
import { withUndefinedAsNull } from '../../../base/common/types.js';
import { keys } from '../../../base/common/map.js';
var TokenizationRegistryImpl = /** @class */ (function () {
    function TokenizationRegistryImpl() {
        this._map = new Map();
        this._promises = new Map();
        this._onDidChange = new Emitter();
        this.onDidChange = this._onDidChange.event;
        this._colorMap = null;
    }
    TokenizationRegistryImpl.prototype.fire = function (languages) {
        this._onDidChange.fire({
            changedLanguages: languages,
            changedColorMap: false
        });
    };
    TokenizationRegistryImpl.prototype.register = function (language, support) {
        var _this = this;
        this._map.set(language, support);
        this.fire([language]);
        return toDisposable(function () {
            if (_this._map.get(language) !== support) {
                return;
            }
            _this._map.delete(language);
            _this.fire([language]);
        });
    };
    TokenizationRegistryImpl.prototype.registerPromise = function (language, supportPromise) {
        var _this = this;
        var registration = null;
        var isDisposed = false;
        this._promises.set(language, supportPromise.then(function (support) {
            _this._promises.delete(language);
            if (isDisposed || !support) {
                return;
            }
            registration = _this.register(language, support);
        }));
        return toDisposable(function () {
            isDisposed = true;
            if (registration) {
                registration.dispose();
            }
        });
    };
    TokenizationRegistryImpl.prototype.getPromise = function (language) {
        var _this = this;
        var support = this.get(language);
        if (support) {
            return Promise.resolve(support);
        }
        var promise = this._promises.get(language);
        if (promise) {
            return promise.then(function (_) { return _this.get(language); });
        }
        return null;
    };
    TokenizationRegistryImpl.prototype.get = function (language) {
        return withUndefinedAsNull(this._map.get(language));
    };
    TokenizationRegistryImpl.prototype.setColorMap = function (colorMap) {
        this._colorMap = colorMap;
        this._onDidChange.fire({
            changedLanguages: keys(this._map),
            changedColorMap: true
        });
    };
    TokenizationRegistryImpl.prototype.getColorMap = function () {
        return this._colorMap;
    };
    TokenizationRegistryImpl.prototype.getDefaultBackground = function () {
        if (this._colorMap && this._colorMap.length > 2 /* DefaultBackground */) {
            return this._colorMap[2 /* DefaultBackground */];
        }
        return null;
    };
    return TokenizationRegistryImpl;
}());
export { TokenizationRegistryImpl };
