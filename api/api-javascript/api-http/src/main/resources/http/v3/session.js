/*
 * Copyright (c) 2010-2020 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
var java = require('core/v3/java');

exports.isValid = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isValid();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isValid();
	}
	var valid = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'isValid', []);
	return valid;
};

exports.getAttribute = function(name) {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getAttribute(name);
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getAttribute(name);
	}
	var attr = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getAttribute', [name]);
	return attr;
};

exports.getAttributeNames = function() {
	var attrNames;
	attrNames = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getAttributeNames', []);
	if (attrNames) {
		return JSON.parse(attrNames);
	}
	return attrNames;
};

exports.getCreationTime = function() {
	var time;
	if (__engine === 'rhino') {
		time = org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getCreationTime();
	} else if (__engine === 'nashorn') {
		time = Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getCreationTime();
	} else {
		time = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getCreationTime', []);
	}
	return new Date(time);
};

exports.getId = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getId();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getId();
	}
	var id = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getId', []);
	return id;
};

exports.getLastAccessedTime = function() {
	var time;
	if (__engine === 'rhino') {
		time = org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getLastAccessedTime();
	} else if (__engine === 'nashorn') {
		time = Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getLastAccessedTime();
	} else {
		time = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getLastAccessedTime', []);
	}
	return new Date(time);
};

exports.getMaxInactiveInterval = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getMaxInactiveInterval();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getMaxInactiveInterval();
	}
	var interval = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'getMaxInactiveInterval', []);
	return interval;
};

exports.invalidate = function() {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpSessionFacade.invalidate();
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.invalidate();
	} else {
		java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'invalidate', []);
	}
};

exports.isNew = function() {
	if (__engine === 'rhino') {
		return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isNew();
	}
	if (__engine === 'nashorn') {
		return Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isNew();
	}
	var result = java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'isNew', []);
	return result;
};

exports.setAttribute = function(name, value) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setAttribute(name, value);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setAttribute(name, value);
	} else {
		java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'setAttribute', [name, value]);
	}
};

exports.removeAttribute = function(name) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpSessionFacade.removeAttribute(name);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.removeAttribute(name);
	} else {
		java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'removeAttribute', [name]);
	}
};

exports.setMaxInactiveInterval = function(interval) {
	if (__engine === 'rhino') {
		org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setMaxInactiveInterval(interval);
	} else if (__engine === 'nashorn') {
		Packages.org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setMaxInactiveInterval(interval);
	} else {
		java.call('org.eclipse.dirigible.api.v3.http.HttpSessionFacade', 'setMaxInactiveInterval', [interval]);
	}
};
