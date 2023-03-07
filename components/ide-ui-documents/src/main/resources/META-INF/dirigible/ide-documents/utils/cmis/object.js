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
let cmis = require("cms/cmis");
let cmisSession = cmis.getSession();

exports.getObject = function (path) {
	try {
		if (path === null || path === undefined) {
			return null;
		}
		return cmisSession.getObjectByPath(path);
	} catch (e) {
		console.error(`Error [${e.message}] in getting an object by path [${path}]`);
	}
	return null;
};

exports.existObject = function (path) {
	try {
		if (path === null || path === undefined) {
			return null;
		}
		return cmisSession.getObjectByPath(path) != null;
	} catch (e) {
		return false
	}
};

exports.getById = function (id) {
	return cmisSession.getObject(id);
};

exports.deleteObject = function (object) {
	object.delete();
};

exports.renameObject = function (object, newName) {
	object.rename(newName);
};