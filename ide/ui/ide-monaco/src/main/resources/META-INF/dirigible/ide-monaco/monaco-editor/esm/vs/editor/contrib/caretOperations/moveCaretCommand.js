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
var MoveCaretCommand = /** @class */ (function () {
    function MoveCaretCommand(selection, isMovingLeft) {
        this._selection = selection;
        this._isMovingLeft = isMovingLeft;
        this._cutStartIndex = -1;
        this._cutEndIndex = -1;
        this._moved = false;
        this._selectionId = null;
    }
    MoveCaretCommand.prototype.getEditOperations = function (model, builder) {
        var s = this._selection;
        this._selectionId = builder.trackSelection(s);
        if (s.startLineNumber !== s.endLineNumber) {
            return;
        }
        if (this._isMovingLeft && s.startColumn === 0) {
            return;
        }
        else if (!this._isMovingLeft && s.endColumn === model.getLineMaxColumn(s.startLineNumber)) {
            return;
        }
        var lineNumber = s.selectionStartLineNumber;
        var lineContent = model.getLineContent(lineNumber);
        var left;
        var middle;
        var right;
        if (this._isMovingLeft) {
            left = lineContent.substring(0, s.startColumn - 2);
            middle = lineContent.substring(s.startColumn - 1, s.endColumn - 1);
            right = lineContent.substring(s.startColumn - 2, s.startColumn - 1) + lineContent.substring(s.endColumn - 1);
        }
        else {
            left = lineContent.substring(0, s.startColumn - 1) + lineContent.substring(s.endColumn - 1, s.endColumn);
            middle = lineContent.substring(s.startColumn - 1, s.endColumn - 1);
            right = lineContent.substring(s.endColumn);
        }
        var newLineContent = left + middle + right;
        builder.addEditOperation(new Range(lineNumber, 1, lineNumber, model.getLineMaxColumn(lineNumber)), null);
        builder.addEditOperation(new Range(lineNumber, 1, lineNumber, 1), newLineContent);
        this._cutStartIndex = s.startColumn + (this._isMovingLeft ? -1 : 1);
        this._cutEndIndex = this._cutStartIndex + s.endColumn - s.startColumn;
        this._moved = true;
    };
    MoveCaretCommand.prototype.computeCursorState = function (model, helper) {
        var result = helper.getTrackedSelection(this._selectionId);
        if (this._moved) {
            result = result.setStartPosition(result.startLineNumber, this._cutStartIndex);
            result = result.setEndPosition(result.startLineNumber, this._cutEndIndex);
        }
        return result;
    };
    return MoveCaretCommand;
}());
export { MoveCaretCommand };
