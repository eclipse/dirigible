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
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
import { FastDomNode } from '../../../base/browser/fastDomNode.js';
import { ViewEventHandler } from '../../common/viewModel/viewEventHandler.js';
var ViewPart = /** @class */ (function (_super) {
    __extends(ViewPart, _super);
    function ViewPart(context) {
        var _this = _super.call(this) || this;
        _this._context = context;
        _this._context.addEventHandler(_this);
        return _this;
    }
    ViewPart.prototype.dispose = function () {
        this._context.removeEventHandler(this);
        _super.prototype.dispose.call(this);
    };
    return ViewPart;
}(ViewEventHandler));
export { ViewPart };
var PartFingerprints = /** @class */ (function () {
    function PartFingerprints() {
    }
    PartFingerprints.write = function (target, partId) {
        if (target instanceof FastDomNode) {
            target.setAttribute('data-mprt', String(partId));
        }
        else {
            target.setAttribute('data-mprt', String(partId));
        }
    };
    PartFingerprints.read = function (target) {
        var r = target.getAttribute('data-mprt');
        if (r === null) {
            return 0 /* None */;
        }
        return parseInt(r, 10);
    };
    PartFingerprints.collect = function (child, stopAt) {
        var result = [], resultLen = 0;
        while (child && child !== document.body) {
            if (child === stopAt) {
                break;
            }
            if (child.nodeType === child.ELEMENT_NODE) {
                result[resultLen++] = this.read(child);
            }
            child = child.parentElement;
        }
        var r = new Uint8Array(resultLen);
        for (var i = 0; i < resultLen; i++) {
            r[i] = result[resultLen - i - 1];
        }
        return r;
    };
    return PartFingerprints;
}());
export { PartFingerprints };
