/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var bytes = require("io/v4/bytes");

exports.getContent = function(path) {
	var nativeContent = org.eclipse.dirigible.api.v3.platform.RegistryFacade.getContent(path);
	return bytes.toJavaScriptBytes(nativeContent);
};

exports.getText = function(path) {
	return org.eclipse.dirigible.api.v3.platform.RegistryFacade.getText(path);
};

exports.find = function(path, pattern) {
	return JSON.parse(org.eclipse.dirigible.api.v3.platform.RegistryFacade.find(path, pattern));
};


