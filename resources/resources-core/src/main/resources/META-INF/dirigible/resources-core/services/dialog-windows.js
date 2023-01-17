/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
let extensions = require('core/v4/extensions');
let response = require('http/v4/response');
let request = require('http/v4/request');
let uuid = require("utils/v4/uuid");

let dialogWindows = [];
let extensionPoint = request.getParameter('extensionPoint') || 'ide-dialog-window';
let dialogWindowExtensions = extensions.getExtensions(extensionPoint);

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
setETag();
response.println(JSON.stringify(dialogWindows));

function setETag() {
    let maxAge = 30 * 24 * 60 * 60;
    let etag = uuid.random();
    response.setHeader("ETag", etag);
    response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}