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

const allThemes = [];
const themeExtensions = extensions.getExtensions('ide-themes');
try {
	for (let i = 0; i < themeExtensions?.length; i++) {
		try {
			let themeExtension = await import(themeExtensions[i]);
			allThemes.push(themeExtension.getTheme());
		} catch (e) {
			// Fallback for not migrated extensions
			let themeExtension = require(themeExtensions[i]);
			allThemes.push(themeExtension.getTheme());
		}
	}
} catch (e) {
	console.error('Error while loading theme modules: ' + e);
}

export const getThemes = (legacy = true) => {
	let themes = [];
	for (const theme of allThemes) {
		if (legacy) {
			if (!('type' in theme)) themes.push(theme);
		}
		else if ('type' in theme) themes.push(theme);
	}
	return sort(themes);
};

function sort(themes) {
	return themes.sort(function (a, b) {
		if (a.order !== undefined && b.order !== undefined) {
			return a.order - b.order;
		} else if (a.order !== undefined) {
			return -1;
		} else if (b.order !== undefined) {
			return 1;
		} else if (a.name > b.name) {
			return 1;
		} else if (a.name < b.name) {
			return -1;
		}
		return 0;
	});
}
