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
import { response } from "@dirigible/http";
import { uuid } from "@dirigible/utils";

let editors = [];
let editorExtensions = extensions.getExtensions('ide-editor');

function setETag() {
	let maxAge = 30 * 24 * 60 * 60;
	let etag = uuid.random();
	response.setHeader("ETag", etag);
	response.setHeader('Cache-Control', `public, must-revalidate, max-age=${maxAge}`);
}

for (let i = 0; i < editorExtensions?.length; i++) {
	let module = editorExtensions[i];
	try {
		try {
			const editorExtension = await import(`../../${module}`);
			editors.push(editorExtension.getEditor());
		} catch (e) {
			// Fallback for not migrated extensions
			const editorExtension = require(module);
			editors.push(editorExtension.getEditor());
		}

		let duplication = false;
		for (let i = 0; i < editors.length; i++) {
			for (let j = 0; j < editors.length; j++) {
				if (i !== j) {
					if (editors[i].id === editors[j].id) {
						if (editors[i].link !== editors[j].link) {
							console.error('Duplication at editor with id: [' + editors[i].id + '] pointing to links: ['
								+ editors[i].link + '] and [' + editors[j].link + ']');
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
		console.error('Error occured while loading metadata for the editor: ' + module);
		console.error(error);
	}
}

editors = editors.sort(function (a, b) {
	if (a.label && b.label) {
		const res = a.label.localeCompare(b.label);
		if (res <= -1) return -1;
		else if (res >= 1) return 1;
	} else {
		let eId;
		if (!a.label) eId = a.id;
		else eId = b.id;
		console.error("Editor with id '" + eId + "' does not have a label.");
	}
	return 0;
});

response.setContentType("application/json");
setETag();
response.println(JSON.stringify(editors));
