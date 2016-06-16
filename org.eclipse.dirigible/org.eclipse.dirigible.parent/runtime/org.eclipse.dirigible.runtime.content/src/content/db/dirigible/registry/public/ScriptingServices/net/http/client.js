/*******************************************************************************
 * Copyright (c) 2015 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * SAP - initial API and implementation
 *******************************************************************************/

/* globals $ java */
/* eslint-env node, dirigible */

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
	for (var nextHeader in headers) {
		httpRequest.addHeader(nextHeader, headers[nextHeader]);
	}
}

function addBody(request, options) {
	if (options.body) {
		var charset = options.charset ? options.charset: "ISO-8859-1";
		request.setEntity($.getHttpUtils().createHttpEntity(options.body, charset));
	}
}

function createResponse(httpResponse, options) {
	return {
		'statusCode': httpResponse.getStatusLine().getStatusCode(),
		'statusMessage': httpResponse.getStatusLine().getReasonPhrase(),
		'data': getResponseData(httpResponse, options),
		'httpVersion': httpResponse.getProtocolVersion(),
		'headers': getResponseHeaders(httpResponse)
	};
}

function getResponseData(httpResponse, options) {
    var entity = httpResponse.getEntity();
    var content = entity.getContent();

    var data = $.getIOUtils().toByteArray(content);

    $.getHttpUtils().consume(entity);
    var isBinary = false;
    if (options) {
        isBinary = options.binary;
    }

    return isBinary ? data : new java.lang.String(data);
}

function getResponseHeaders(httpResponse) {
	var headers = [];
	var httpResponseHeaders = httpResponse.getAllHeaders();
	for (var i = 0; i < httpResponseHeaders.length; i ++) {
		var header = httpResponseHeaders[i];
		headers.push({
			'name': header.getName(),
			'value': header.getValue()
		});
	}
	return headers;
}
