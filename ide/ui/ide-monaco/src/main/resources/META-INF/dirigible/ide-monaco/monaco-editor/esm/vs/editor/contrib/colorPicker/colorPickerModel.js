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
var ColorPickerModel = /** @class */ (function () {
    function ColorPickerModel(color, availableColorPresentations, presentationIndex) {
        this.presentationIndex = presentationIndex;
        this._onColorFlushed = new Emitter();
        this.onColorFlushed = this._onColorFlushed.event;
        this._onDidChangeColor = new Emitter();
        this.onDidChangeColor = this._onDidChangeColor.event;
        this._onDidChangePresentation = new Emitter();
        this.onDidChangePresentation = this._onDidChangePresentation.event;
        this.originalColor = color;
        this._color = color;
        this._colorPresentations = availableColorPresentations;
    }
    Object.defineProperty(ColorPickerModel.prototype, "color", {
        get: function () {
            return this._color;
        },
        set: function (color) {
            if (this._color.equals(color)) {
                return;
            }
            this._color = color;
            this._onDidChangeColor.fire(color);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ColorPickerModel.prototype, "presentation", {
        get: function () { return this.colorPresentations[this.presentationIndex]; },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(ColorPickerModel.prototype, "colorPresentations", {
        get: function () {
            return this._colorPresentations;
        },
        set: function (colorPresentations) {
            this._colorPresentations = colorPresentations;
            if (this.presentationIndex > colorPresentations.length - 1) {
                this.presentationIndex = 0;
            }
            this._onDidChangePresentation.fire(this.presentation);
        },
        enumerable: true,
        configurable: true
    });
    ColorPickerModel.prototype.selectNextColorPresentation = function () {
        this.presentationIndex = (this.presentationIndex + 1) % this.colorPresentations.length;
        this.flushColor();
        this._onDidChangePresentation.fire(this.presentation);
    };
    ColorPickerModel.prototype.guessColorPresentation = function (color, originalText) {
        for (var i = 0; i < this.colorPresentations.length; i++) {
            if (originalText === this.colorPresentations[i].label) {
                this.presentationIndex = i;
                this._onDidChangePresentation.fire(this.presentation);
                break;
            }
        }
    };
    ColorPickerModel.prototype.flushColor = function () {
        this._onColorFlushed.fire(this._color);
    };
    return ColorPickerModel;
}());
export { ColorPickerModel };
