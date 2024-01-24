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
import { extensions } from "@dirigible/extensions";
import { request, response } from "@dirigible/http";
import { uuid } from "@dirigible/utils";

const customActions = [];
const extensionPoint = request.getParameter('extensionPoint');
const customActionExtensions = await extensions.loadExtensionModules(extensionPoint);

function setETag() {
    const maxAge = 30 * 24 * 60 * 60;
    const etag = uuid.random();
    response.setHeader("ETag", etag);
    response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}

for (let i = 0; i < customActionExtensions?.length; i++) {
    customActions.push(customActionExtensions[i].getAction());
}

customActions.sort(function (a, b) {
    if (a.order !== undefined && b.order !== undefined) {
        return (parseInt(a.order) - parseInt(b.order));
    } else if (a.order === undefined && b.order === undefined) {
        return a.label < b.label ? -1 : 1
    } else if (a.order === undefined) {
        return 1;
    } else if (b.order === undefined) {
        return -1;
    }
    return 0;
});

response.setContentType("application/json");
setETag();
response.println(JSON.stringify(customActions));
