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
var java = require('core/v3/java');

exports.get = function(key, defaultValue) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.commons.config.Configuration.get(key, defaultValue);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.commons.config.Configuration.get(key, defaultValue);
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
