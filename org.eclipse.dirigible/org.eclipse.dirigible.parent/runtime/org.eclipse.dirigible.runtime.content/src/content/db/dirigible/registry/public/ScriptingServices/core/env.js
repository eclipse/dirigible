/*******************************************************************************
 * Copyright (c) 2016 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java javax */
/* eslint-env node, dirigible */

/**
 * Getter for environment variable by key
 */
exports.get = function(key) {
	return java.lang.System.getProperty(key);
};

/**
 * Setter for environment variable by key and value
 */
exports.set = function(key, value) {
	java.lang.System.setProperty(key, value);
};

/**
 * Getter for all the environment variables in JSON
 */
exports.getAll = function() {
	var list = [];
	var internalList = java.lang.System.getProperties().entrySet().iterator();
	while (internalList.hasNext()) {
		var entry = {};
		var internalEntry = internalList.next();
		entry.key = internalEntry.getKey();
		entry.value = internalEntry.getValue();
		list.push(entry);
	}
	return list;
};

exports.getOperatingSystemName = function() {
	return java.lang.System.getProperty('os.name');
};

exports.getOperatingSystemArchitecture = function() {
	return java.lang.System.getProperty('os.arch');
};

exports.getOperatingSystemVersion = function() {
	return java.lang.System.getProperty('os.version');
};

exports.getFileSeparator = function() {
	return java.lang.System.getProperty('file.separator');
};

exports.getPathSeparator = function() {
	return java.lang.System.getProperty('path.separator');
};

exports.getLineSeparator = function() {
	return java.lang.System.getProperty('line.separator');
};

exports.getUserDirectory = function() {
	return java.lang.System.getProperty('user.dir');
};

exports.getUserHome = function() {
	return java.lang.System.getProperty('user.home');
};

exports.getUserName = function() {
	return java.lang.System.getProperty('user.name');
};
