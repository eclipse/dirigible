/*
 * Copyright (c) 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2021 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
var java = require('core/v3/java');

exports.random = function() {
	var uuid = java.call('org.eclipse.dirigible.api.v3.utils.UuidFacade', 'random', []);
	return uuid;
};

exports.validate = function(input) {
	var valid = java.call('org.eclipse.dirigible.api.v3.utils.UuidFacade', 'validate', [input]);
	return valid;
};

