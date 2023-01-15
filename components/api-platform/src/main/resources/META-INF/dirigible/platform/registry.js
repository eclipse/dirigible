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
const bytes = require("io/bytes");

exports.getContent = function(path) {
	const nativeContent = org.eclipse.dirigible.components.api.platform.RegistryFacade.getContent(path);
	return bytes.toJavaScriptBytes(nativeContent);
};

exports.getContentNative = function(path) {
	return org.eclipse.dirigible.components.api.platform.RegistryFacade.getContent(path);
};

exports.getText = function(path) {
	return org.eclipse.dirigible.components.api.platform.RegistryFacade.getText(path);
};

exports.find = function(path, pattern) {
	return JSON.parse(org.eclipse.dirigible.components.api.platform.RegistryFacade.find(path, pattern));
};
