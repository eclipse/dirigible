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
import { Emitter } from '../common/event.js';
var WindowManager = /** @class */ (function () {
    function WindowManager() {
        // --- Zoom Level
        this._zoomLevel = 0;
        this._lastZoomLevelChangeTime = 0;
        this._onDidChangeZoomLevel = new Emitter();
        this.onDidChangeZoomLevel = this._onDidChangeZoomLevel.event;
    }
    WindowManager.prototype.getZoomLevel = function () {
        return this._zoomLevel;
    };
    WindowManager.prototype.getTimeSinceLastZoomLevelChanged = function () {
        return Date.now() - this._lastZoomLevelChangeTime;
    };
    // --- Pixel Ratio
    WindowManager.prototype.getPixelRatio = function () {
        var ctx = document.createElement('canvas').getContext('2d');
        var dpr = window.devicePixelRatio || 1;
        var bsr = ctx.webkitBackingStorePixelRatio ||
            ctx.mozBackingStorePixelRatio ||
            ctx.msBackingStorePixelRatio ||
            ctx.oBackingStorePixelRatio ||
            ctx.backingStorePixelRatio || 1;
        return dpr / bsr;
    };
    WindowManager.INSTANCE = new WindowManager();
    return WindowManager;
}());
export function getZoomLevel() {
    return WindowManager.INSTANCE.getZoomLevel();
}
/** Returns the time (in ms) since the zoom level was changed */
export function getTimeSinceLastZoomLevelChanged() {
    return WindowManager.INSTANCE.getTimeSinceLastZoomLevelChanged();
}
export function onDidChangeZoomLevel(callback) {
    return WindowManager.INSTANCE.onDidChangeZoomLevel(callback);
}
export function getPixelRatio() {
    return WindowManager.INSTANCE.getPixelRatio();
}
var userAgent = navigator.userAgent;
export var isIE = (userAgent.indexOf('Trident') >= 0);
export var isEdge = (userAgent.indexOf('Edge/') >= 0);
export var isEdgeOrIE = isIE || isEdge;
export var isFirefox = (userAgent.indexOf('Firefox') >= 0);
export var isWebKit = (userAgent.indexOf('AppleWebKit') >= 0);
export var isChrome = (userAgent.indexOf('Chrome') >= 0);
export var isSafari = (!isChrome && (userAgent.indexOf('Safari') >= 0));
export var isWebkitWebView = (!isChrome && !isSafari && isWebKit);
export var isIPad = (userAgent.indexOf('iPad') >= 0 || (isSafari && navigator.maxTouchPoints > 0));
export var isEdgeWebView = isEdge && (userAgent.indexOf('WebView/') >= 0);
export var isStandalone = (window.matchMedia && window.matchMedia('(display-mode: standalone)').matches);
