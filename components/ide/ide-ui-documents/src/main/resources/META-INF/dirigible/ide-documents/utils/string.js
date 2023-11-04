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
exports.replaceAll = function (string, find, replace) {
	return string.replace(new RegExp(find, 'g'), replace);
};

exports.unescapePath = function (path) {
	return path.replace(/\\/g, '');
};

exports.getNameFromPath = function (path) {
	let splittedFullName = path.split("/");
	let name = splittedFullName[splittedFullName.length - 1];
	return (!name || name.lenght === 0) ? "root" : name;
};

exports.formatPath = function (path) {
	path = exports.replaceAll(path, "//", "/");
	if (!path.startsWith("/")) {
		path = "/" + path;
	}
	if (path.endsWith("/")) {
		path = path.substr(0, path.length - 1);
	}
	return path;
}