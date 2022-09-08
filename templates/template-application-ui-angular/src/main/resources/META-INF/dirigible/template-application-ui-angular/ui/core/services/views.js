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

const EXTENSION_POINT_VIEWS = "${projectName}-view";

let views = [];
let viewExtensions = extensions.getExtensions(EXTENSION_POINT_VIEWS);

for (let i = 0; i < viewExtensions.length; i++) {
	let module = viewExtensions[i];
	try {
		let viewExtension = require(module);
		let view = viewExtension.getView();
		views.push(view);

		let duplication = false;
		for (let i = 0; i < views.length; i++) {
			for (let j = 0; j < views.length; j++) {
				if (i !== j) {
					if (views[i].id === views[j].id) {
						if (views[i].link !== views[j].link) {
							console.error('Duplication at view with id: [' + views[i].id + '] pointing to links: ['
								+ views[i].link + '] and [' + views[j].link + ']');
						}
						duplication = true;
						break;
					}
				}
			}
			if (duplication) {
				break;
			}
		}
	} catch (error) {
		console.error('Error occured while loading metadata for the view: ' + module);
		console.error(error);
	}
}
response.setContentType("application/json");
response.println(JSON.stringify(views));