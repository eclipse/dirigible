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
 * HTTP API ClientAsync
 *
 */

const HttpClientAsyncFacade = Java.type("org.eclipse.dirigible.components.api.http.HttpClientAsyncFacade");
const SpringBeanProvider = Java.type("org.eclipse.dirigible.components.spring.SpringBeanProvider");
const httpClient = SpringBeanProvider.getBean(HttpClientAsyncFacade.class);


interface Config {
	success: Function;
	error: Function;
	cancel: Function;
}

interface Option {
	params: Array<{name: string, value: string}>;
}

export class HttpAsyncClient {
	public static getAsync(url: string, config: Config, options: Option): void{
		const newUrl = this.buildUrl(url, options);
		const callback =  this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.getAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.getAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static postAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.postAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.postAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static putAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.putAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.putAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static patchAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.patchAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.patchAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static deleteAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.deleteAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.deleteAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static headAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.headAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.headAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static traceAsync(url: string, config: Config, options: Option): void {
		const newUrl = this.buildUrl(url, options);
		const callback = this.createHttpResponseCallback(
			httpClient,
			config.success,
			config.error,
			config.cancel
		);
		if (options) {
			httpClient.traceAsync(newUrl, JSON.stringify(options), callback);
		} else {
			httpClient.traceAsync(newUrl, JSON.stringify({}), callback);
		}
	};

	public static execute(): void {
		httpClient.execute();
	};


	public static getInstance(): HttpAsyncClient {
		return new HttpAsyncClient();
	};

	private static createHttpResponseCallback(httpClient_: typeof httpClient, successCallback: Function, errorCallback: Function, cancelCallback: Function): typeof httpClient{
		return httpClient_.createCallback(
			this.createSuccessCallback(successCallback),
			this.createErrorCallback(errorCallback),
			this.createCancelCallback(cancelCallback)
		);
	}

	private static createSuccessCallback(callback: Function): string {
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

	private static createErrorCallback(callback: Function): string {
		if (callback) {
			return "(" + callback + ")(__context.get('exception'))";
		}
	}

	private static createCancelCallback(callback: Function): string {
		if (callback) {
			return "(" + callback + ")()";
		}
	}

	private static buildUrl(url: string, options: Option): string {
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
}