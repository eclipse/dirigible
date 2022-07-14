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
const request = require("http/v4/request");
const extensions = require('core/v4/extensions');
const response = require('http/v4/response');

const menuExtensionId = request.getParameter("id");
let mainmenu = [];
let menuExtensions = extensions.getExtensions(menuExtensionId);

for (let i = 0; i < menuExtensions.length; i++) {
	let module = menuExtensions[i];
	try {
		menuExtension = require(module);
		let menu = menuExtension.getMenu();
		mainmenu.push(menu);
	} catch (error) {
		console.error('Error occured while loading metadata for the menu: ' + module);
		console.error(error);
	}
}

mainmenu.sort(function (p, n) {
	return (parseInt(p.order) - parseInt(n.order));
});
response.setContentType("application/json");
response.println(JSON.stringify(mainmenu));