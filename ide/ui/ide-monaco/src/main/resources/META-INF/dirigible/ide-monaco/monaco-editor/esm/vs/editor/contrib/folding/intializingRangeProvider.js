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
import { sanitizeRanges } from './syntaxRangeProvider.js';
export var ID_INIT_PROVIDER = 'init';
var InitializingRangeProvider = /** @class */ (function () {
    function InitializingRangeProvider(editorModel, initialRanges, onTimeout, timeoutTime) {
        this.editorModel = editorModel;
        this.id = ID_INIT_PROVIDER;
        if (initialRanges.length) {
            var toDecorationRange = function (range) {
                return {
                    range: {
                        startLineNumber: range.startLineNumber,
                        startColumn: 0,
                        endLineNumber: range.endLineNumber,
                        endColumn: editorModel.getLineLength(range.endLineNumber)
                    },
                    options: {
                        stickiness: 1 /* NeverGrowsWhenTypingAtEdges */
                    }
                };
            };
            this.decorationIds = editorModel.deltaDecorations([], initialRanges.map(toDecorationRange));
            this.timeout = setTimeout(onTimeout, timeoutTime);
        }
    }
    InitializingRangeProvider.prototype.dispose = function () {
        if (this.decorationIds) {
            this.editorModel.deltaDecorations(this.decorationIds, []);
            this.decorationIds = undefined;
        }
        if (typeof this.timeout === 'number') {
            clearTimeout(this.timeout);
            this.timeout = undefined;
        }
    };
    InitializingRangeProvider.prototype.compute = function (cancelationToken) {
        var foldingRangeData = [];
        if (this.decorationIds) {
            for (var _i = 0, _a = this.decorationIds; _i < _a.length; _i++) {
                var id = _a[_i];
                var range = this.editorModel.getDecorationRange(id);
                if (range) {
                    foldingRangeData.push({ start: range.startLineNumber, end: range.endLineNumber, rank: 1 });
                }
            }
        }
        return Promise.resolve(sanitizeRanges(foldingRangeData, Number.MAX_VALUE));
    };
    return InitializingRangeProvider;
}());
export { InitializingRangeProvider };
