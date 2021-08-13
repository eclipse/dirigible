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
export var EditorZoom = new /** @class */ (function () {
    function class_1() {
        this._zoomLevel = 0;
        this._onDidChangeZoomLevel = new Emitter();
        this.onDidChangeZoomLevel = this._onDidChangeZoomLevel.event;
    }
    class_1.prototype.getZoomLevel = function () {
        return this._zoomLevel;
    };
    class_1.prototype.setZoomLevel = function (zoomLevel) {
        zoomLevel = Math.min(Math.max(-5, zoomLevel), 20);
        if (this._zoomLevel === zoomLevel) {
            return;
        }
        this._zoomLevel = zoomLevel;
        this._onDidChangeZoomLevel.fire(this._zoomLevel);
    };
    return class_1;
}());
