/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
let extensions = require('core/v4/extensions');
let response = require('http/v4/response');

let dialogWindows = [];
let dialogWindowExtensions = extensions.getExtensions('ide-dialog-window');

for (let i = 0; i < dialogWindowExtensions.length; i++) {
    let module = dialogWindowExtensions[i];
    try {
        let dialogWindowExtension = require(module);
        let window = dialogWindowExtension.getDialogWindow();
        dialogWindows.push(window);
    } catch (error) {
        console.error('Error occured while loading metadata for the window: ' + module);
        console.error(error);
    }
}

dialogWindows.sort(function (p, n) {
    return (parseInt(p.order) - parseInt(n.order));
});
response.setContentType("application/json");
response.println(JSON.stringify(dialogWindows));