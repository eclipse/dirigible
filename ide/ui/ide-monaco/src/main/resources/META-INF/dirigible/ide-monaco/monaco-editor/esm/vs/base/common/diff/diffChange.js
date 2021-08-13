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
/**
 * Represents information about a specific difference between two sequences.
 */
var DiffChange = /** @class */ (function () {
    /**
     * Constructs a new DiffChange with the given sequence information
     * and content.
     */
    function DiffChange(originalStart, originalLength, modifiedStart, modifiedLength) {
        //Debug.Assert(originalLength > 0 || modifiedLength > 0, "originalLength and modifiedLength cannot both be <= 0");
        this.originalStart = originalStart;
        this.originalLength = originalLength;
        this.modifiedStart = modifiedStart;
        this.modifiedLength = modifiedLength;
    }
    /**
     * The end point (exclusive) of the change in the original sequence.
     */
    DiffChange.prototype.getOriginalEnd = function () {
        return this.originalStart + this.originalLength;
    };
    /**
     * The end point (exclusive) of the change in the modified sequence.
     */
    DiffChange.prototype.getModifiedEnd = function () {
        return this.modifiedStart + this.modifiedLength;
    };
    return DiffChange;
}());
export { DiffChange };
