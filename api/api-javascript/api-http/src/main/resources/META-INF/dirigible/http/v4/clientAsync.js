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
 * API v4 ClientAsync
 *
 * Note: This module is supported only with the Mozilla Rhino engine
 */

function createSuccessCallback(callback) {
	return "(function(httpResponse, isBinary, context) {\n"
		+ "var response = {};\n"
		+ "response.statusCode = httpResponse.getStatusLine().getStatusCode();\n"
		+ "response.statusMessage = httpResponse.getStatusLine().getReasonPhrase();\n"
		+ "response.protocol = httpResponse.getProtocolVersion();\n"
		+ "response.binary = isBinary;\n"

		+ "var headers = httpResponse.getAllHeaders();\n"
		+ "response.headers = [];\n"
		+ "for (var i = 0; i < headers.length; i ++) {\n"
		+ "    response.headers.push({\n"
		+ "        name: headers[i].getName(),\n"
		+ "        value: headers[i].getValue()\n"
		+ "    });\n"
		+ "}\n"

		+ "var entity = httpResponse.getEntity();\n"
		+ "if (entity) {\n"
		+ "    var inputStream = entity.getContent();\n"
		+ "    if (isBinary) {\n"
		+ "        response.data = org.apache.commons.io.IOUtils.toByteArray(inputStream);\n"
		+ "    } else {\n"
		+ "        response.text = org.apache.commons.io.IOUtils.toString(inputStream);\n"
		+ "    }\n"
		+ "}\n"

		+ "(" + callback + ")(response, JSON.parse(context));\n"
		+ "})(__context.get('response'), __context.get('httpClientRequestOptions').isBinary(), __context.get('httpClientRequestOptions').getContext());\n";
}

function createErrorCallback(callback) {
	if (callback) {
		return "(" + callback + ")(__context.get('exception'))";
	}
}

function createCancelCallback(callback) {
	if (callback) {
		return "(" + callback + ")()";
	}
}

function createHttpResponseCallback(httpClient, successCallback, errorCallback, cancelCallback) {
	return httpClient.createCallback(
		createSuccessCallback(successCallback),
		createErrorCallback(errorCallback),
		createCancelCallback(cancelCallback)
	);
}

function buildUrl(url, options) {
	if (options === undefined || options === null || options.params === undefined || options.params === null || options.params.length === 0) {
		return url;
	}
	let newUrl = url;
	for (let i = 0; i < options.params.length; i ++) {
		if (i === 0) {
			newUrl += '?' + options.params[i].name + '=' + options.params[i].value;
		} else {
			newUrl += '&' + options.params[i].name + '=' + options.params[i].value;
		}
	}
	return newUrl;
}

function HttpAsyncClient() {

	this.httpClient = new org.eclipse.dirigible.api.v3.http.HttpClientAsyncFacade();

	this.getAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.getAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.getAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.postAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.postAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.postAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.putAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.putAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.putAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.patchAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.patchAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.patchAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.deleteAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.deleteAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.deleteAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.headAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.headAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.headAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.traceAsync = function(url, config, options) {
		const newUrl = buildUrl(url, options);
		const callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.traceAsync(newUrl, JSON.stringify(options), callback);
		} else {
			this.httpClient.traceAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	this.execute = function() {
		this.httpClient.execute();
	};
}

exports.getInstnace = function() {
	return new HttpAsyncClient();
};
