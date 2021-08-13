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
import { Range } from '../core/range.js';
/**
 * Contains all data needed to render at a specific viewport.
 */
var ViewportData = /** @class */ (function () {
    function ViewportData(selections, partialData, whitespaceViewportData, model) {
        this.selections = selections;
        this.startLineNumber = partialData.startLineNumber | 0;
        this.endLineNumber = partialData.endLineNumber | 0;
        this.relativeVerticalOffset = partialData.relativeVerticalOffset;
        this.bigNumbersDelta = partialData.bigNumbersDelta | 0;
        this.whitespaceViewportData = whitespaceViewportData;
        this._model = model;
        this.visibleRange = new Range(partialData.startLineNumber, this._model.getLineMinColumn(partialData.startLineNumber), partialData.endLineNumber, this._model.getLineMaxColumn(partialData.endLineNumber));
    }
    ViewportData.prototype.getViewLineRenderingData = function (lineNumber) {
        return this._model.getViewLineRenderingData(this.visibleRange, lineNumber);
    };
    ViewportData.prototype.getDecorationsInViewport = function () {
        return this._model.getDecorationsInViewport(this.visibleRange);
    };
    return ViewportData;
}());
export { ViewportData };
