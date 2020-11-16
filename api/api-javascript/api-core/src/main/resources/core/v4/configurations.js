/*
 * Copyright (c) 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2010-2020 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Configurations
 * 
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.get = function(key, defaultValue) {
	if (defaultValue) {
		return org.eclipse.dirigible.commons.config.Configuration.get(key, defaultValue);
	}
	return org.eclipse.dirigible.commons.config.Configuration.get(key);
};

exports.set = function(key, value) {
	org.eclipse.dirigible.commons.config.Configuration.set(key, value);
};

exports.remove = function(key) {
	org.eclipse.dirigible.commons.config.Configuration.remove(key);
};

exports.getKeys = function() {
	return org.eclipse.dirigible.commons.config.Configuration.getKeys();
};

exports.load = function(path) {
	org.eclipse.dirigible.commons.config.Configuration.load(path);
};

exports.update = function() {
	org.eclipse.dirigible.commons.config.Configuration.update();
};
