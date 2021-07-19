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

exports.get = function(key, defaultValue) {
	if (__engine === 'rhino') {
		if (defaultValue) {
			return org.eclipse.dirigible.commons.config.Configuration.get(key, defaultValue);
		}
		return org.eclipse.dirigible.commons.config.Configuration.get(key);
	}
	if (__engine === 'nashorn') {
		if (defaultValue) {
			return Packages.org.eclipse.dirigible.commons.config.Configuration.get(key, defaultValue);
		}
		return Packages.org.eclipse.dirigible.commons.config.Configuration.get(key);
	}
	return java.call('org.eclipse.dirigible.commons.config.Configuration', 'get', [key, defaultValue]);
};

exports.set = function(key, value) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.commons.config.Configuration.set(key, value);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.commons.config.Configuration.set(key, value);
	} else {
		java.call('org.eclipse.dirigible.commons.config.Configuration', 'set', [key, value]);
	}
};

exports.remove = function(key) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.commons.config.Configuration.remove(key);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.commons.config.Configuration.remove(key);
	} else {
		java.call('org.eclipse.dirigible.commons.config.Configuration', 'remove', [key]);
	}
};

exports.getKeys = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.commons.config.Configuration.getKeys();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.commons.config.Configuration.getKeys();
	}
	return java.call('org.eclipse.dirigible.commons.config.Configuration', 'getKeys', []);
};

exports.load = function(path) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.commons.config.Configuration.load(path);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.commons.config.Configuration.load(path);
	} else {
		java.call('org.eclipse.dirigible.commons.config.Configuration', 'load', [path]);
	}
};

exports.update = function() {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.commons.config.Configuration.update();
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.commons.config.Configuration.update();
	} else {
		java.call('org.eclipse.dirigible.commons.config.Configuration', 'update', []);
	}
};
