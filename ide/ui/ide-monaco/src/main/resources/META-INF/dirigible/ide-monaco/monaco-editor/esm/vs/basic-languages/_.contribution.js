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
'use strict';
// Allow for running under nodejs/requirejs in tests
var _monaco = (typeof monaco === 'undefined' ? self.monaco : monaco);
var languageDefinitions = {};
var lazyLanguageLoaders = {};
var LazyLanguageLoader = /** @class */ (function () {
    function LazyLanguageLoader(languageId) {
        var _this = this;
        this._languageId = languageId;
        this._loadingTriggered = false;
        this._lazyLoadPromise = new Promise(function (resolve, reject) {
            _this._lazyLoadPromiseResolve = resolve;
            _this._lazyLoadPromiseReject = reject;
        });
    }
    LazyLanguageLoader.getOrCreate = function (languageId) {
        if (!lazyLanguageLoaders[languageId]) {
            lazyLanguageLoaders[languageId] = new LazyLanguageLoader(languageId);
        }
        return lazyLanguageLoaders[languageId];
    };
    LazyLanguageLoader.prototype.whenLoaded = function () {
        return this._lazyLoadPromise;
    };
    LazyLanguageLoader.prototype.load = function () {
        var _this = this;
        if (!this._loadingTriggered) {
            this._loadingTriggered = true;
            languageDefinitions[this._languageId].loader().then(function (mod) { return _this._lazyLoadPromiseResolve(mod); }, function (err) { return _this._lazyLoadPromiseReject(err); });
        }
        return this._lazyLoadPromise;
    };
    return LazyLanguageLoader;
}());
export function loadLanguage(languageId) {
    return LazyLanguageLoader.getOrCreate(languageId).load();
}
export function registerLanguage(def) {
    var languageId = def.id;
    languageDefinitions[languageId] = def;
    _monaco.languages.register(def);
    var lazyLanguageLoader = LazyLanguageLoader.getOrCreate(languageId);
    _monaco.languages.setMonarchTokensProvider(languageId, lazyLanguageLoader.whenLoaded().then(function (mod) { return mod.language; }));
    _monaco.languages.onLanguage(languageId, function () {
        lazyLanguageLoader.load().then(function (mod) {
            _monaco.languages.setLanguageConfiguration(languageId, mod.conf);
        });
    });
}
