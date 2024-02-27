/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
import { extensions } from "sdk/extensions";
import { request, response } from "sdk/http";
import { uuid } from "sdk/utils";

let dialogWindows = [];
let extensionPoint = request.getParameter('extensionPoint') || 'ide-dialog-window';
let dialogWindowExtensions = await extensions.loadExtensionModules(extensionPoint);

function setETag() {
    let maxAge = 30 * 24 * 60 * 60;
    let etag = uuid.random();
    response.setHeader("ETag", etag);
    response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}

for (let i = 0; i < dialogWindowExtensions?.length; i++) {
    dialogWindows.push(dialogWindowExtensions[i].getDialogWindow());
}

dialogWindows.sort(function (p, n) {
    return (parseInt(p.order) - parseInt(n.order));
});

response.setContentType("application/json");
setETag();
response.println(JSON.stringify(dialogWindows));
