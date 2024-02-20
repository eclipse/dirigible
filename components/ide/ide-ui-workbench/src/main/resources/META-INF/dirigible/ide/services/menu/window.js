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
// Deprecated, do not edit.

import { extensions } from 'sdk/extensions';

const perspectiveExtensions = await extensions.loadExtensionModules('ide-perspective');
const perspectiveExtensionDefinitions = [];

for (let i = 0; i < perspectiveExtensions?.length; i++) {
	perspectiveExtensionDefinitions.push(perspectiveExtensions[i].getPerspective());
}

const viewExtensions = await extensions.loadExtensionModules('ide-view');
const viewExtensionDefinitions = [];
for (let i = 0; i < viewExtensions?.length; i++) {
	viewExtensionDefinitions.push(viewExtensions[i].getView());
}

export const getMenu = () => {
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

	viewExtensionDefinitions = viewExtensionDefinitions.sort((a, b) => a.label.toLowerCase().localeCompare(b.label.toLowerCase()));
	for (let i = 0; i < viewExtensionDefinitions.length; i++) {
		let viewInfo = viewExtensionDefinitions[i];
		let viewMenu = {
			"name": viewInfo.label,
			"link": "#",
			"order": "" + (820 + i),
			"onClick": "window.open('" + viewInfo.link + "', '_blank')"
		};
		menu.items[1].items.push(viewMenu);
	}

	return menu;
}