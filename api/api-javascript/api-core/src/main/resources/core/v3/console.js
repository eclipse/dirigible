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

exports.log = function(message) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.log(message);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.log(message);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'log', [message]);
	}
};

exports.error = function(message, args) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.error(message, args);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.error(message, args);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'error', [message, args]);
	}
};

exports.info = function(message, args) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.info(message, args);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.info(message, args);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'info', [message, args]);
	}
};

exports.warn = function(message, args) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.warn(message, args);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.warn(message, args);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'warn', [message, args]);
	}
};

exports.debug = function(message, args) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.debug(message, args);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.debug(message, args);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'debug', [message, args]);
	}
};

exports.trace = function(message, args) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.core.ConsoleFacade.trace(message, args);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.core.ConsoleFacade.trace(message, args);
	} else {
		java.call('org.eclipse.dirigible.api.v3.core.ConsoleFacade', 'trace', [message, args]);
	}
};
