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

let perspectives = [];
let extensionPoint = request.getParameter('extensionPoint') || 'ide-perspective';
let perspectiveExtensions = extensions.getExtensions(extensionPoint);

function setETag() {
	let maxAge = 30 * 24 * 60 * 60;
	let etag = uuid.random();
	response.setHeader("ETag", etag);
	response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}

for (let i = 0; i < perspectiveExtensions?.length; i++) {
	let module = perspectiveExtensions[i];
	try {
		try {
			const perspectiveExtension = await import(`../../${module}`);
			perspectives.push(perspectiveExtension.getPerspective());
		} catch (e) {
			// Fallback for not migrated extensions
			const perspectiveExtension = require(module);
			perspectives.push(perspectiveExtension.getPerspective());
		}

		let duplication = false;
		for (let i = 0; i < perspectives.length; i++) {
			for (let j = 0; j < perspectives.length; j++) {
				if (i !== j) {
					if (perspectives[i].name === perspectives[j].name) {
						if (perspectives[i].link !== perspectives[j].link) {
							console.error('Duplication at perspective with name: [' + perspectives[i].name + '] pointing to links: ['
								+ perspectives[i].link + '] and [' + perspectives[j].link + ']');
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
		console.error('Error occured while loading metadata for the perspective: ' + module);
		console.error(error);
	}
}

perspectives.sort(function (p, n) {
	return (parseInt(p.order) - parseInt(n.order));
});
response.setContentType("application/json");
setETag();
response.println(JSON.stringify(perspectives));
