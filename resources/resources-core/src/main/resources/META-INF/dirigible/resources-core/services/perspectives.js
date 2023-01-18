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
let uuid = require('utils/v4/uuid');

let perspectives = [];
let extensionPoint = request.getParameter('extensionPoint') || 'ide-perspective';
let perspectiveExtensions = extensions.getExtensions(extensionPoint);

for (let i = 0; i < perspectiveExtensions.length; i++) {
	let module = perspectiveExtensions[i];
	try {
		let perspectiveExtension = require(module);
		let perspective = perspectiveExtension.getPerspective();
		perspectives.push(perspective);

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

function setETag() {
	let maxAge = 30 * 24 * 60 * 60;
	let etag = uuid.random();
	response.setHeader("ETag", etag);
	response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}