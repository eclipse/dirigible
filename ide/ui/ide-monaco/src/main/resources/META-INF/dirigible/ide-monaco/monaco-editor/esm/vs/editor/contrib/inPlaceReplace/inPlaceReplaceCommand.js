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
import { Selection } from '../../common/core/selection.js';
var InPlaceReplaceCommand = /** @class */ (function () {
    function InPlaceReplaceCommand(editRange, originalSelection, text) {
        this._editRange = editRange;
        this._originalSelection = originalSelection;
        this._text = text;
    }
    InPlaceReplaceCommand.prototype.getEditOperations = function (model, builder) {
        builder.addTrackedEditOperation(this._editRange, this._text);
    };
    InPlaceReplaceCommand.prototype.computeCursorState = function (model, helper) {
        var inverseEditOperations = helper.getInverseEditOperations();
        var srcRange = inverseEditOperations[0].range;
        if (!this._originalSelection.isEmpty()) {
            // Preserve selection and extends to typed text
            return new Selection(srcRange.endLineNumber, srcRange.endColumn - this._text.length, srcRange.endLineNumber, srcRange.endColumn);
        }
        return new Selection(srcRange.endLineNumber, Math.min(this._originalSelection.positionColumn, srcRange.endColumn), srcRange.endLineNumber, Math.min(this._originalSelection.positionColumn, srcRange.endColumn));
    };
    return InPlaceReplaceCommand;
}());
export { InPlaceReplaceCommand };
