/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var extensions = require('core/v4/extensions');

exports.getThemes = function() {
	var themes = [];
	try {
		var themeExtensions = extensions.getExtensions('ide-themes');
		for (var i = 0; themeExtensions  !== null && i < themeExtensions .length; i++) {
			var themeExtension = require(themeExtensions[i]);
			var theme = themeExtension.getTheme();
			themes.push(theme);
		}
		themes = sort(themes);
	} catch (e) {
		console.error('Error while loading theme modules: ' + e);
	}
	return themes;
};

function sort(themes) {
	return themes.sort(function(a, b) {
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
