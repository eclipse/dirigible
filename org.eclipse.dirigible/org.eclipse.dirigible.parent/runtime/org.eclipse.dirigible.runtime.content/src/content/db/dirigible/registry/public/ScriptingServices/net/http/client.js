/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java engine */
/* eslint-env node, dirigible */

var streams = require("io/streams");

exports.get = function(url, options) {
	return handleRequest(url, 'GET', options);
};

exports.post = function(url, options) {
	return handleRequest(url, 'POST', options);
};

exports.put = function(url, options) {
	return handleRequest(url, 'PUT', options);
};

exports.delete = function(url, options) {
	return handleRequest(url, 'DELETE', options);
};

exports.request = function(options) {
	return handleRequest(null, options.method, options);
};

function handleRequest(url, method, options) {
    if (url === null) {
	    url = options.host + ":" + (options.port ? options.port : 80) + (options.path ? options.path : '/');
    }

    if (method === null) {
        method = "GET";
    }

	var request = createRequest(method, url, options);

	var httpClient = $.getHttpUtils().createHttpClient(true);
	return createResponse(httpClient.execute(request), options);
}

function createRequest(method, url, options) {
	var request = null;
	switch(method) {
		case 'POST':
			request = $.getHttpUtils().createPost(url);
			addBody(request, options);
			break;
		case 'PUT':
			request = $.getHttpUtils().createPut(url);
			addBody(request, options);
			break;
		case 'DELETE':
			request = $.getHttpUtils().createDelete(url);
			break;
		default:
			request = $.getHttpUtils().createGet(url);
	}
	if (options) {
	    addHeaders(request, options.headers);
    }
	return request;
}

function addHeaders(httpRequest, headers) {
	if (headers) {
		for (var i = 0; i < headers.length; ++i) {
			httpRequest.addHeader(headers[i].name, headers[i].value);
		}
	}
}

function addBody(request, options) {
	if (options.body) {
		var charset = options.charset ? options.charset: "ISO-8859-1";
		request.setEntity($.getHttpUtils().createHttpEntity(options.body, charset));
	}
}

function createResponse(httpResponse, options) {
	var response = new HttpResponse();
	response.statusCode = httpResponse.getStatusLine().getStatusCode();
	response.statusMessage = httpResponse.getStatusLine().getReasonPhrase();
	response.data = getResponseData(httpResponse, options);
	response.httpVersion = httpResponse.getProtocolVersion();
	response.headers = getResponseHeaders(httpResponse);
	return response;
}

function getResponseData(httpResponse, options) {
    var entity = httpResponse.getEntity();
    var content = entity.getContent();
    
    var data;
	if (engine === "nashorn") {
		data = $.getIOUtils().class.static.toByteArray(content);
	} else {
		data = $.getIOUtils().toByteArray(content);
	}

    $.getHttpUtils().consume(entity);
    var isBinary = false;
    if (options) {
        isBinary = options.binary;
    }

    return isBinary ? streams.toJavaScriptBytes(data) : new java.lang.String(data);
}

function getResponseHeaders(httpResponse) {
	var headers = [];
	var httpResponseHeaders = httpResponse.getAllHeaders();
	for (var i = 0; i < httpResponseHeaders.length; i ++) {
		var internalHeader = httpResponseHeaders[i];
		var header = new HttpHeader();
		header.name = internalHeader.getName();
		header.value = internalHeader.getValue();
		headers.push(header);
	}
	return headers;
}

/**
 * HTTP Response object
 */
function HttpResponse() {
	this.statusCode = 0;
	this.statusMessage = "";
	this.data = [];
	this.httpVersion = "";
	this.headers = [];
}

/**
 * HTTP Header object
 */
function HttpHeader() {
	this.name = "";
	this.value = "";
}

/**
 * HTTP Option object
 */
function HttpOptions() {
	this.host = "";
	this.port = 0;
	this.method = "";
	this.charset = "";
	this.headers = [];
}
