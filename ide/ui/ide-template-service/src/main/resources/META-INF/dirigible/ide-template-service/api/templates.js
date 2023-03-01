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
let extensions = require('core/v4/extensions');
let response = require('http/v4/response');

let rs = require("http/v4/rs");

rs.service()
	.resource("")
	.get(function (ctx, request, response) {
		let templates = getTemplates();
		templates.sort(sortTemplates);
		response.setContentType("application/json");
		response.println(JSON.stringify(templates));
	})
	.resource("extensions")
	.get(function (ctx, request, response) {
		let templates = getTemplates();
		let fileExtensions = [];
		templates.forEach(template => { if (template.extension) fileExtensions.push(template.extension); });
		response.setContentType("application/json");
		response.println(JSON.stringify(fileExtensions));
	})
	.resource("menu")
	.get(function (ctx, request, response) {
		let templates = getTemplates(true);
		templates.sort(sortTemplates);
		response.setContentType("application/json");
		response.println(JSON.stringify(templates));
	})
	.execute();

function getTemplates(forMenu = false) {
	let templates = [];
	let templateExtensions;
	if (forMenu) templateExtensions = extensions.getExtensions('ide-workspace-menu-new-template');
	else templateExtensions = extensions.getExtensions('ide-template');
	for (let i = 0; i < templateExtensions.length; i++) {
		let module = templateExtensions[i];
		try {
			let templateExtension = require(module);
			let template = templateExtension.getTemplate();
			template.id = module;
			templates.push(template);
		} catch (error) {
			console.error('Error occured while loading metadata for the template: ' + module);
			console.error(error);
		}
	}
	return templates;
}

function sortTemplates(a, b) {
	if (a.hasOwnProperty('order') && b.hasOwnProperty('order')) {
		return a.order - b.order;
	} else if (a.hasOwnProperty('order') === true && b.order === undefined) {
		return -1;
	} else if (b.hasOwnProperty('order') === true && a.order === undefined) {
		return 1;
	}

	if (a.label && b.label) {
		let result = a.label.toLowerCase().localeCompare(b.label.toLowerCase());
		if (result < 0) return -1;
		else if (result > 0) return 1;
		else return 0;
	}

	if (a.name > b.name) {
		return 1;
	} else if (a.name < b.name) {
		return -1;
	}
	return 0;
}