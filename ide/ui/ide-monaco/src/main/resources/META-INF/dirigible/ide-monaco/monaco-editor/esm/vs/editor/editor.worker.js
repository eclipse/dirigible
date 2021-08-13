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
import { SimpleWorkerServer } from '../base/common/worker/simpleWorker.js';
import { EditorSimpleWorker } from './common/services/editorSimpleWorker.js';
var initialized = false;
export function initialize(foreignModule) {
    if (initialized) {
        return;
    }
    initialized = true;
    var simpleWorker = new SimpleWorkerServer(function (msg) {
        self.postMessage(msg);
    }, function (host) { return new EditorSimpleWorker(host, foreignModule); });
    self.onmessage = function (e) {
        simpleWorker.onmessage(e.data);
    };
}
self.onmessage = function (e) {
    // Ignore first message in this case and initialize if not yet initialized
    if (!initialized) {
        initialize(null);
    }
};
