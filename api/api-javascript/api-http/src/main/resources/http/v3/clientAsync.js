/*
 * Copyright (c) 2010-2019 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   SAP - initial API and implementation
 */
function createSuccessCallback(callback) {
	return "(function(httpResponse) {\n"
		+ "var response = {};\n"
		+ "response.statusCode = httpResponse.getStatusLine().getStatusCode();\n"
		+ "response.headers = httpResponse.getAllHeaders();\n"
		+ "var entity = httpResponse.getEntity();\n"
		+ "if (entity) {\n"
		+ "var inputStream = entity.getContent();\n"
		+ "var content = org.apache.commons.io.IOUtils.toString(inputStream);\n"
		+ "response.data = content;\n"
		+ "}\n"
		+ "(" + callback + ")(response);\n"
		+ "})(__context.get('response'));\n";
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

function HttpAsyncClient() {
	
	this.httpClient = new org.eclipse.dirigible.engine.js.rhino.api.v3.http.HttpClientAsync();

	this.getAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.getAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.getAsync(url, JSON.stringify({}), callback);
		}
	};

	this.postAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.postAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.postAsync(url, JSON.stringify({}), callback);
		}
	};

	this.putAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.putAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.putAsync(url, JSON.stringify({}), callback);
		}
	};

	this.deleteAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.deleteAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.deleteAsync(url, JSON.stringify({}), callback);
		}
	};

	this.headAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.headAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.headAsync(url, JSON.stringify({}), callback);
		}
	};

	this.traceAsync = function(url, config, options) {
		var callback = createHttpResponseCallback(
			this.httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			this.httpClient.traceAsync(url, JSON.stringify(options), callback);
		} else {
			this.httpClient.traceAsync(url, JSON.stringify({}), callback);
		}
	};

	this.execute = function() {
		this.httpClient.execute();
	};
}

exports.getInstnace = function() {
	return new HttpAsyncClient();
};