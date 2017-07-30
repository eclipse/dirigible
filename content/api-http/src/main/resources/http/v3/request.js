/*******************************************************************************
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* eslint-env node, dirigible */

var java = require('core/v3/java');

exports.getMethod = function() {
	var method = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getMethod', []);
	return method;
};

exports.getRemoteUser = function() {
	var remoteUser = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getRemoteUser', []);
	return remoteUser;
};

exports.getPathInfo = function() {
	var pathInfo = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getPathInfo', []);
	return pathInfo;
};

exports.getHeader = function(name) {
	var header = java.call('org.eclipse.dirigible.api.v3.http.HttpRequestFacade', 'getHeader', [name]);
	return header;
};
