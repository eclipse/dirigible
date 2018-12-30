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

exports.isValid = function() {
	var valid = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'isValid', []);
	return valid;
};

exports.getAttribute = function(name) {
	var attr = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getAttribute', [name]);
	return attr;
};

exports.getAttributeNames = function() {
	var attrNames = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getAttributeNames', []);
	if (attrNames) {
		return JSON.parse(attrNames);
	}
	return attrNames;
};

exports.getCreationTime = function() {
	var time = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getCreationTime', []);
	return new Date(time);
};

exports.getId = function() {
	var id = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getId', []);
	return id;
};

exports.getLastAccessedTime = function() {
	var time = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getLastAccessedTime', []);
	return new Date(time);
};

exports.getMaxInactiveInterval = function() {
	var interval = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getMaxInactiveInterval', []);
	return interval;
};

exports.invalidate = function() {
	java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'invalidate', []);
};

exports.isNew = function() {
	var result = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'isNew', []);
	return result;
};

exports.setAttribute = function(name, value) {
	java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'setAttribute', [name, value]);
};

exports.removeAttribute = function(name) {
	java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'removeAttribute', [name]);
};

exports.setMaxInactiveInterval = function(interval) {
	java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'setMaxInactiveInterval', [interval]);
};


