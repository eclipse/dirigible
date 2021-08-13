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
var ElementsDragAndDropData = /** @class */ (function () {
    function ElementsDragAndDropData(elements) {
        this.elements = elements;
    }
    ElementsDragAndDropData.prototype.update = function (dataTransfer) {
        // no-op
    };
    ElementsDragAndDropData.prototype.getData = function () {
        return this.elements;
    };
    return ElementsDragAndDropData;
}());
export { ElementsDragAndDropData };
var ExternalElementsDragAndDropData = /** @class */ (function () {
    function ExternalElementsDragAndDropData(elements) {
        this.elements = elements;
    }
    ExternalElementsDragAndDropData.prototype.update = function (dataTransfer) {
        // no-op
    };
    ExternalElementsDragAndDropData.prototype.getData = function () {
        return this.elements;
    };
    return ExternalElementsDragAndDropData;
}());
export { ExternalElementsDragAndDropData };
var DesktopDragAndDropData = /** @class */ (function () {
    function DesktopDragAndDropData() {
        this.types = [];
        this.files = [];
    }
    DesktopDragAndDropData.prototype.update = function (dataTransfer) {
        if (dataTransfer.types) {
            this.types = [];
            Array.prototype.push.apply(this.types, dataTransfer.types);
        }
        if (dataTransfer.files) {
            this.files = [];
            Array.prototype.push.apply(this.files, dataTransfer.files);
            this.files = this.files.filter(function (f) { return f.size || f.type; });
        }
    };
    DesktopDragAndDropData.prototype.getData = function () {
        return {
            types: this.types,
            files: this.files
        };
    };
    return DesktopDragAndDropData;
}());
export { DesktopDragAndDropData };
