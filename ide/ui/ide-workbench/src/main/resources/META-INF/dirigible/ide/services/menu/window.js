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
let extensions = require('core/v4/extensions');

exports.getMenu = function () {
	let menu = {
		"name": "Window",
		"link": "#",
		"order": "800",
		"onClick": "alert('Window has been clicked')",
		"items": [
			{
				"name": "Open Perspective",
				"link": "#",
				"order": "810",
				"items": []
			},
			{
				"name": "Show View",
				"link": "#",
				"order": "820",
				"items": [],
				"divider": true
			},
			{
				"name": "Reset",
				"link": "",
				"order": "830"
			}
		]
	};



	let perspectiveExtensions = extensions.getExtensions('ide-perspective');
	let perspectiveExtensionDefinitions = [];

	for (let i = 0; i < perspectiveExtensions.length; i++) {
		let module = perspectiveExtensions[i];
		perspectiveExtensionDefinitions.push(require(module).getPerspective());
	}
	perspectiveExtensionDefinitions = perspectiveExtensionDefinitions.sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
	for (let i = 0; i < perspectiveExtensionDefinitions.length; i++) {
		let perspectiveInfo = perspectiveExtensionDefinitions[i];
		let perspectiveMenu = {
			"name": perspectiveInfo.name,
			"link": "#",
			"order": "" + (810 + i),
			"onClick": "window.open('" + perspectiveInfo.link + "', '_blank')"
		};
		menu.items[0].items.push(perspectiveMenu);
	}

	let viewExtensions = extensions.getExtensions('ide-view');
	let viewExtensionDefinitions = [];
	for (let i = 0; i < viewExtensions.length; i++) {
		let module = viewExtensions[i];
		viewExtensionDefinitions.push(require(module).getView());
	}
	viewExtensionDefinitions = viewExtensionDefinitions.sort((a, b) => a.name.toLowerCase().localeCompare(b.name.toLowerCase()));
	for (let i = 0; i < viewExtensionDefinitions.length; i++) {
		let viewInfo = viewExtensionDefinitions[i];
		let viewMenu = {
			"name": viewInfo.name,
			"link": "#",
			"order": "" + (820 + i),
			"onClick": "window.open('" + viewInfo.link + "', '_blank')"
		};
		menu.items[1].items.push(viewMenu);
	}

	return menu;
}