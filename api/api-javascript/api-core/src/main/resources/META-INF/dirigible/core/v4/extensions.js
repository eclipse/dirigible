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
/**
 * API v4 Extensions
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.getExtensions = function(extensionPoint) {
	const extensions = org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade.getExtensions(extensionPoint);
	return JSON.parse(JSON.stringify(extensions));
};

exports.getExtensionPoints = function() {
	const extensionPoints = org.eclipse.dirigible.api.v3.core.ExtensionsServiceFacade.getExtensionPoints();
	return JSON.parse(JSON.stringify(extensionPoints));
};
