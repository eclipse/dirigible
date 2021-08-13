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
import { EditOperation } from '../../common/core/editOperation.js';
import { Range } from '../../common/core/range.js';
var SortLinesCommand = /** @class */ (function () {
    function SortLinesCommand(selection, descending) {
        this.selection = selection;
        this.descending = descending;
        this.selectionId = null;
    }
    SortLinesCommand.getCollator = function () {
        if (!SortLinesCommand._COLLATOR) {
            SortLinesCommand._COLLATOR = new Intl.Collator();
        }
        return SortLinesCommand._COLLATOR;
    };
    SortLinesCommand.prototype.getEditOperations = function (model, builder) {
        var op = sortLines(model, this.selection, this.descending);
        if (op) {
            builder.addEditOperation(op.range, op.text);
        }
        this.selectionId = builder.trackSelection(this.selection);
    };
    SortLinesCommand.prototype.computeCursorState = function (model, helper) {
        return helper.getTrackedSelection(this.selectionId);
    };
    SortLinesCommand.canRun = function (model, selection, descending) {
        if (model === null) {
            return false;
        }
        var data = getSortData(model, selection, descending);
        if (!data) {
            return false;
        }
        for (var i = 0, len = data.before.length; i < len; i++) {
            if (data.before[i] !== data.after[i]) {
                return true;
            }
        }
        return false;
    };
    SortLinesCommand._COLLATOR = null;
    return SortLinesCommand;
}());
export { SortLinesCommand };
function getSortData(model, selection, descending) {
    var startLineNumber = selection.startLineNumber;
    var endLineNumber = selection.endLineNumber;
    if (selection.endColumn === 1) {
        endLineNumber--;
    }
    // Nothing to sort if user didn't select anything.
    if (startLineNumber >= endLineNumber) {
        return null;
    }
    var linesToSort = [];
    // Get the contents of the selection to be sorted.
    for (var lineNumber = startLineNumber; lineNumber <= endLineNumber; lineNumber++) {
        linesToSort.push(model.getLineContent(lineNumber));
    }
    var sorted = linesToSort.slice(0);
    sorted.sort(SortLinesCommand.getCollator().compare);
    // If descending, reverse the order.
    if (descending === true) {
        sorted = sorted.reverse();
    }
    return {
        startLineNumber: startLineNumber,
        endLineNumber: endLineNumber,
        before: linesToSort,
        after: sorted
    };
}
/**
 * Generate commands for sorting lines on a model.
 */
function sortLines(model, selection, descending) {
    var data = getSortData(model, selection, descending);
    if (!data) {
        return null;
    }
    return EditOperation.replace(new Range(data.startLineNumber, 1, data.endLineNumber, model.getLineMaxColumn(data.endLineNumber)), data.after.join('\n'));
}
