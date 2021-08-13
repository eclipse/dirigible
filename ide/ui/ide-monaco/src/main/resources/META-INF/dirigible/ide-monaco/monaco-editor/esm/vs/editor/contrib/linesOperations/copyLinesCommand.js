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
import { Range } from '../../common/core/range.js';
import { Selection } from '../../common/core/selection.js';
var CopyLinesCommand = /** @class */ (function () {
    function CopyLinesCommand(selection, isCopyingDown) {
        this._selection = selection;
        this._isCopyingDown = isCopyingDown;
        this._selectionDirection = 0 /* LTR */;
        this._selectionId = null;
        this._startLineNumberDelta = 0;
        this._endLineNumberDelta = 0;
    }
    CopyLinesCommand.prototype.getEditOperations = function (model, builder) {
        var s = this._selection;
        this._startLineNumberDelta = 0;
        this._endLineNumberDelta = 0;
        if (s.startLineNumber < s.endLineNumber && s.endColumn === 1) {
            this._endLineNumberDelta = 1;
            s = s.setEndPosition(s.endLineNumber - 1, model.getLineMaxColumn(s.endLineNumber - 1));
        }
        var sourceLines = [];
        for (var i = s.startLineNumber; i <= s.endLineNumber; i++) {
            sourceLines.push(model.getLineContent(i));
        }
        var sourceText = sourceLines.join('\n');
        if (sourceText === '') {
            // Duplicating empty line
            if (this._isCopyingDown) {
                this._startLineNumberDelta++;
                this._endLineNumberDelta++;
            }
        }
        if (!this._isCopyingDown) {
            builder.addEditOperation(new Range(s.endLineNumber, model.getLineMaxColumn(s.endLineNumber), s.endLineNumber, model.getLineMaxColumn(s.endLineNumber)), '\n' + sourceText);
        }
        else {
            builder.addEditOperation(new Range(s.startLineNumber, 1, s.startLineNumber, 1), sourceText + '\n');
        }
        this._selectionId = builder.trackSelection(s);
        this._selectionDirection = this._selection.getDirection();
    };
    CopyLinesCommand.prototype.computeCursorState = function (model, helper) {
        var result = helper.getTrackedSelection(this._selectionId);
        if (this._startLineNumberDelta !== 0 || this._endLineNumberDelta !== 0) {
            var startLineNumber = result.startLineNumber;
            var startColumn = result.startColumn;
            var endLineNumber = result.endLineNumber;
            var endColumn = result.endColumn;
            if (this._startLineNumberDelta !== 0) {
                startLineNumber = startLineNumber + this._startLineNumberDelta;
                startColumn = 1;
            }
            if (this._endLineNumberDelta !== 0) {
                endLineNumber = endLineNumber + this._endLineNumberDelta;
                endColumn = 1;
            }
            result = Selection.createWithDirection(startLineNumber, startColumn, endLineNumber, endColumn, this._selectionDirection);
        }
        return result;
    };
    return CopyLinesCommand;
}());
export { CopyLinesCommand };
