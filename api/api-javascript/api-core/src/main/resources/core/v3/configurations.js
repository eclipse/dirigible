/*
 * Copyright (c) 2010-2018 SAP and others.
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
	return java.call('org.eclipse.dirigible.commons.config.Configuration', 'get', [key, defaultValue]);
};

exports.set = function(key, value) {
	java.call('org.eclipse.dirigible.commons.config.Configuration', 'set', [key, value]);
};

exports.getKeys = function() {
	return java.call('org.eclipse.dirigible.commons.config.Configuration', 'getKeys', []);
};

exports.load = function(path) {
	java.call('org.eclipse.dirigible.commons.config.Configuration', 'load', [path]);
};

exports.update = function() {
	java.call('org.eclipse.dirigible.commons.config.Configuration', 'update', []);
};

