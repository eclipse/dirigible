/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

exports.get = function(path, key) {
	try {
		var value = $.getConfigurationStorage().getProperty(path, key);
	} catch(e) {
		console(e.message);
		return null;
	}
	return new java.lang.String(value === null ? "" : value);
};

exports.get1 = function(path, key) {
	try {
		var value = $.getConfigurationStorage().getProperty(path, key);
	} catch(e) {
		console(e.message);
		return null;
	}
	return new java.lang.String(value === null ? "" : value);
};

exports.set = function(path, key, value) {
	$.getConfigurationStorage().putProperty(path, key, value);
};

exports.delete = function(path) {
	$.getConfigurationStorage().delete(path);
};

exports.clear = function() {
	$.getConfigurationStorage().clear();
};

exports.getJson = function(path) {
	try {
		var value = $.getConfigurationStorage().getJson(path);
	} catch(e) {
		return null;
	}
	return new java.lang.String(value === null ? "" : value);
};

exports.setJson = function(path, json) {
	$.getConfigurationStorage().putJson(path, json);
};
