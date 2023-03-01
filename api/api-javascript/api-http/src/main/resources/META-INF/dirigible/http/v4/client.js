/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-FileCopyrightText: 2023 SAP SE or an SAP affiliate company and Eclipse Dirigible contributors
 * SPDX-License-Identifier: EPL-2.0
 */
/**
 * API v4 Client
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

exports.get = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.get(url, opts);
	return JSON.parse(result);
};

exports.post = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.post(url, opts);
	return JSON.parse(result);
};

exports.put = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.put(url, opts);
	return JSON.parse(result);
};

exports.patch = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.patch(url, opts);
	return JSON.parse(result);
};

exports.delete = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.delete(url, opts);
	return JSON.parse(result);
};

exports.head = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.head(url, opts);
	return JSON.parse(result);
};

exports.trace = function(_url, options) {
	let url = buildUrl(_url, options);
	let result = {};
	let opts = '{}';
	if (options) {
		opts = JSON.stringify(options);
	}
	result = org.eclipse.dirigible.api.v3.http.HttpClientFacade.trace(url, opts);
	return JSON.parse(result);
};

function buildUrl(url, options) {
	if (options === undefined || options === null || options.params === undefined || options.params === null || options.params.length === 0) {
		return url;
	}
	for (let i = 0; i < options.params.length; i ++) {
		if (i === 0) {
			url += '?' + options.params[i].name + '=' + options.params[i].value;
		} else {
			url += '&' + options.params[i].name + '=' + options.params[i].value;
		}
	}
	return url;
}
