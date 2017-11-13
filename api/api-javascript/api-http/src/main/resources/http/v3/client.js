/*
 * Copyright (c) 2017 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 */

var java = require('core/v3/java');

request = function(url, method, options) {
	var result = {};
	if (options) {
		result = java.call('org.eclipse.dirigible.api.v3.http.HttpClientFacade', method, [url, JSON.stringify(options)]);
	} else {
		result = java.call('org.eclipse.dirigible.api.v3.http.HttpClientFacade', method, [url, '{}']);
	}
	
	return JSON.parse(result);
};

exports.get = function(url, options) {
	return request(url, 'get', options);
};

exports.post = function(url, options) {
	return request(url, 'post', options);
};

exports.put = function(url, options) {
	return request(url, 'put', options);
};

exports.delete = function(url, options) {
	return request(url, 'delete', options);
};

exports.head = function(url, options) {
	return request(url, 'head', options);
};

exports.trace = function(url, options) {
	return request(url, 'trace', options);
};



