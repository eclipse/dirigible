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
import { Range } from './range.js';
var EditOperation = /** @class */ (function () {
    function EditOperation() {
    }
    EditOperation.insert = function (position, text) {
        return {
            range: new Range(position.lineNumber, position.column, position.lineNumber, position.column),
            text: text,
            forceMoveMarkers: true
        };
    };
    EditOperation.delete = function (range) {
        return {
            range: range,
            text: null
        };
    };
    EditOperation.replace = function (range, text) {
        return {
            range: range,
            text: text
        };
    };
    EditOperation.replaceMove = function (range, text) {
        return {
            range: range,
            text: text,
            forceMoveMarkers: true
        };
    };
    return EditOperation;
}());
export { EditOperation };
