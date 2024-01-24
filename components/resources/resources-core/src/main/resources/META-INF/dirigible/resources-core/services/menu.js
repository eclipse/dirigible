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
import { request, response } from "@dirigible/http";
import { extensions } from "@dirigible/extensions";
import { uuid } from "@dirigible/utils";

const menuExtensionId = request.getParameter("id");
let mainmenu = [];
let menuExtensions = await extensions.loadExtensionModules(menuExtensionId);

function setETag() {
	const maxAge = 30 * 24 * 60 * 60;
	const etag = uuid.random();
	response.setHeader("ETag", etag);
	response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}

for (let i = 0; i < menuExtensions?.length; i++) {
	mainmenu.push(menuExtensions[i].getMenu());
}

mainmenu.sort(function (p, n) {
	return (parseInt(p.order) - parseInt(n.order));
});
response.setContentType("application/json");
setETag();
response.println(JSON.stringify(mainmenu));
