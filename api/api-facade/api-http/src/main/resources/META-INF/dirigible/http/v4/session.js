/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2022 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Session
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.isValid = function() {
	return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isValid();
};

exports.getAttribute = function(name) {
	return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getAttribute(name);
};

exports.getAttributeNames = function() {
	const attrNames = org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getAttributeNamesJson();
	if (attrNames) {
		return JSON.parse(attrNames);
	}
	return attrNames;
};

exports.getCreationTime = function() {
	const time = org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getCreationTime();
	return new Date(time);
};

exports.getId = function() {
	return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getId();
};

exports.getLastAccessedTime = function() {
	const time = org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getLastAccessedTime();
	return new Date(time);
};

exports.getMaxInactiveInterval = function() {
	return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.getMaxInactiveInterval();
};

exports.invalidate = function() {
	org.eclipse.dirigible.api.v3.http.HttpSessionFacade.invalidate();
};

exports.isNew = function() {
	return org.eclipse.dirigible.api.v3.http.HttpSessionFacade.isNew();
};

exports.setAttribute = function(name, value) {
	org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setAttribute(name, value);
};

exports.removeAttribute = function(name) {
	org.eclipse.dirigible.api.v3.http.HttpSessionFacade.removeAttribute(name);
};

exports.setMaxInactiveInterval = function(interval) {
	org.eclipse.dirigible.api.v3.http.HttpSessionFacade.setMaxInactiveInterval(interval);
};
