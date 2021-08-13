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
var CSSDataProvider = /** @class */ (function () {
    /**
     * Currently, unversioned data uses the V1 implementation
     * In the future when the provider handles multiple versions of HTML custom data,
     * use the latest implementation for unversioned data
     */
    function CSSDataProvider(data) {
        this._properties = [];
        this._atDirectives = [];
        this._pseudoClasses = [];
        this._pseudoElements = [];
        this.addData(data);
    }
    CSSDataProvider.prototype.provideProperties = function () {
        return this._properties;
    };
    CSSDataProvider.prototype.provideAtDirectives = function () {
        return this._atDirectives;
    };
    CSSDataProvider.prototype.providePseudoClasses = function () {
        return this._pseudoClasses;
    };
    CSSDataProvider.prototype.providePseudoElements = function () {
        return this._pseudoElements;
    };
    CSSDataProvider.prototype.addData = function (data) {
        if (data.properties) {
            this._properties = this._properties.concat(data.properties);
        }
        if (data.atDirectives) {
            this._atDirectives = this._atDirectives.concat(data.atDirectives);
        }
        if (data.pseudoClasses) {
            this._pseudoClasses = this._pseudoClasses.concat(data.pseudoClasses);
        }
        if (data.pseudoElements) {
            this._pseudoElements = this._pseudoElements.concat(data.pseudoElements);
        }
    };
    return CSSDataProvider;
}());
export { CSSDataProvider };
