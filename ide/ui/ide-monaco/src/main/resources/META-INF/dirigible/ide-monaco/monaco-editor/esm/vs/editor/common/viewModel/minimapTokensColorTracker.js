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
import { RGBA8 } from '../core/rgba.js';
import { TokenizationRegistry } from '../modes.js';
var MinimapTokensColorTracker = /** @class */ (function () {
    function MinimapTokensColorTracker() {
        var _this = this;
        this._onDidChange = new Emitter();
        this.onDidChange = this._onDidChange.event;
        this._updateColorMap();
        TokenizationRegistry.onDidChange(function (e) {
            if (e.changedColorMap) {
                _this._updateColorMap();
            }
        });
    }
    MinimapTokensColorTracker.getInstance = function () {
        if (!this._INSTANCE) {
            this._INSTANCE = new MinimapTokensColorTracker();
        }
        return this._INSTANCE;
    };
    MinimapTokensColorTracker.prototype._updateColorMap = function () {
        var colorMap = TokenizationRegistry.getColorMap();
        if (!colorMap) {
            this._colors = [RGBA8.Empty];
            this._backgroundIsLight = true;
            return;
        }
        this._colors = [RGBA8.Empty];
        for (var colorId = 1; colorId < colorMap.length; colorId++) {
            var source = colorMap[colorId].rgba;
            // Use a VM friendly data-type
            this._colors[colorId] = new RGBA8(source.r, source.g, source.b, Math.round(source.a * 255));
        }
        var backgroundLuminosity = colorMap[2 /* DefaultBackground */].getRelativeLuminance();
        this._backgroundIsLight = backgroundLuminosity >= 0.5;
        this._onDidChange.fire(undefined);
    };
    MinimapTokensColorTracker.prototype.getColor = function (colorId) {
        if (colorId < 1 || colorId >= this._colors.length) {
            // background color (basically invisible)
            colorId = 2 /* DefaultBackground */;
        }
        return this._colors[colorId];
    };
    MinimapTokensColorTracker.prototype.backgroundIsLight = function () {
        return this._backgroundIsLight;
    };
    MinimapTokensColorTracker._INSTANCE = null;
    return MinimapTokensColorTracker;
}());
export { MinimapTokensColorTracker };
