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
// Common data transfers
export var DataTransfers = {
    /**
     * Application specific resource transfer type
     */
    RESOURCES: 'ResourceURLs',
    /**
     * Browser specific transfer type to download
     */
    DOWNLOAD_URL: 'DownloadURL',
    /**
     * Browser specific transfer type for files
     */
    FILES: 'Files',
    /**
     * Typically transfer type for copy/paste transfers.
     */
    TEXT: 'text/plain'
};
var DragAndDropData = /** @class */ (function () {
    function DragAndDropData(data) {
        this.data = data;
    }
    DragAndDropData.prototype.update = function () {
        // noop
    };
    DragAndDropData.prototype.getData = function () {
        return this.data;
    };
    return DragAndDropData;
}());
export { DragAndDropData };
export var StaticDND = {
    CurrentDragAndDropData: undefined
};
