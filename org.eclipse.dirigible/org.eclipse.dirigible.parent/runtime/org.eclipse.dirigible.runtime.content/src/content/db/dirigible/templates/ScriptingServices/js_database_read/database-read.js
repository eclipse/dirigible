/* globals $ */
/* eslint-env node, dirigible */

var request = require('net/http/request');
var response = require('net/http/response');
var xss = require('utils/xss');
var ${fileNameNoExtension}Dao = require('${packageName}/dao/${fileNameNoExtension}Dao');

handleRequest(request, response, xss);

function handleRequest(httpRequest, httpResponse, xss) {
		try {
		dispatchRequest(httpRequest, httpResponse, xss);
	} catch (e) {
		console.error(e);
		sendResponse(httpResponse, httpResponse.BAD_REQUEST, 'text/plain', e);
	}
}

function dispatchRequest(httpRequest, httpResponse, xss) {
	httpResponse.setContentType('application/json; charset=UTF-8');
	httpResponse.setCharacterEncoding('UTF-8');

	switch (httpRequest.getMethod()) {
		case 'GET':
			handleGetRequest(httpRequest, httpResponse, xss);
			break;
		default:
			handleNotAllowedRequest(httpResponse);
	}
}

function handleGetRequest(httpRequest, httpResponse, xss) {
	var count = xss.escapeSql(httpRequest.getParameter('count'));
	var metadata = xss.escapeSql(httpRequest.getParameter('metadata'));
	var limit = xss.escapeSql(httpRequest.getParameter('limit'));
	var offset = xss.escapeSql(httpRequest.getParameter('offset'));
	var sort = xss.escapeSql(httpRequest.getParameter('sort'));
	var desc = xss.escapeSql(httpRequest.getParameter('desc'));

	limit = limit ? limit : 100;
	offset = offset ? offset : 0;

	if (!hasConflictingParameters(null, count, metadata, httpResponse)) {
		if (count !== null) {
			var ${fileNameNoExtension}Count = ${fileNameNoExtension}Dao.count();
			sendResponse(httpResponse, httpResponse.OK, 'text/plain', ${fileNameNoExtension}Count);
		} else if (metadata !== null) {
			var ${fileNameNoExtension}Metadata = ${fileNameNoExtension}Dao.metadata();
			sendResponse(httpResponse, httpResponse.OK, 'application/json', JSON.stringify(${fileNameNoExtension}Metadata, null, 2));
		} else {
			var ${fileNameNoExtension} = ${fileNameNoExtension}Dao.list(limit, offset, sort, desc);
			sendResponse(httpResponse, httpResponse.OK, 'application/json', JSON.stringify(${fileNameNoExtension}, null, 2));
		}
	}
}

function handleNotAllowedRequest(httpResponse) {
	sendResponse(httpResponse, httpResponse.METHOD_NOT_ALLOWED);
}

function hasConflictingParameters(id, count, metadata, httpResponse) {
	var result = false;
    if (id !== null && count !== null) {
    	sendResponse(httpResponse, httpResponse.EXPECTATION_FAILED, 'text/plain', 'Expectation failed: conflicting parameters - id, count');
        result = true;
    } else if (id !== null && metadata !== null) {
    	sendResponse(httpResponse, httpResponse.EXPECTATION_FAILED, 'text/plain', 'Expectation failed: conflicting parameters - id, metadata');
        result = true;
    } else if (count !== null && metadata !== null) {
    	sendResponse(httpResponse, httpResponse.EXPECTATION_FAILED, 'text/plain', 'Expectation failed: conflicting parameters - count, metadata');
        result = true;
	}
    return result;
}

function sendResponse(response, status, contentType, content) {
	response.setStatus(status);
	response.setContentType(contentType);
	response.println(content);
	response.flush();
	response.close();	
}
