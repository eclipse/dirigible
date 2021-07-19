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
var java = require('core/v3/java');

exports.get = function(name) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.core.EnvFacade.get(name);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.core.EnvFacade.get(name);
	}
	var value = java.call('org.eclipse.dirigible.api.v3.core.EnvFacade', 'get', [name]);
	return value;
};

exports.list = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.core.EnvFacade.list();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.core.EnvFacade.list();
	}
	var value = java.call('org.eclipse.dirigible.api.v3.core.EnvFacade', 'list', []);
	return value;
};
