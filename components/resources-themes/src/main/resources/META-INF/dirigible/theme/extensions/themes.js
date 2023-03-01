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
let extensions = require('extensions/extensions');

exports.getThemes = function (legacy = true) {
	let themes = [];
	try {
		let themeExtensions = extensions.getExtensions('ide-themes');
		for (let i = 0; themeExtensions !== null && i < themeExtensions.length; i++) {
			let themeExtension = require(themeExtensions[i]);
			let theme = themeExtension.getTheme();
			if (legacy) {
				if (!('type' in theme)) themes.push(theme);
			}
			else if ('type' in theme) themes.push(theme);
		}
		themes = sort(themes);
	} catch (e) {
		console.error('Error while loading theme modules: ' + e);
	}
	return themes;
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
