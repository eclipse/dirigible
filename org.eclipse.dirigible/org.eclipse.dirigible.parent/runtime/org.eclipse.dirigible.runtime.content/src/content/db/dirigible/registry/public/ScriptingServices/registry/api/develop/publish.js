/* globals $ */
/* eslint-env node, dirigible */

var request = require('net/http/request');
var response = require('net/http/response');
var lifecycle = require('platform/lifecycle');


handleRequest(request, response);

function handleRequest(httpRequest, httpResponse, xss) {
    try {
        dispatchRequest(httpRequest, httpResponse, xss);
    } catch (e) {
        console.error(e);
        sendResponse(httpResponse, httpResponse.BAD_REQUEST, 'text/plain', e);
    }
}

function dispatchRequest(httpRequest, httpResponse, xss) {
    response.setContentType('application/json; charset=UTF-8');
    response.setCharacterEncoding('UTF-8');

    switch (httpRequest.getMethod()) {
        case 'POST': 
            handlePostRequest(httpRequest, httpResponse);
            break;
        default:
            handleNotAllowedRequest(httpResponse);
    }
}

function handlePostRequest(httpRequest, httpResponse) {
    var project = getRequestBody(httpRequest);
    if (project !== null && project.length !== 0) {
    	lifecycle.publishProject(project);
    	sendResponse(httpResponse, httpResponse.OK, 'text/plain', project + " has been published successfully.");
	} else {
		lifecycle.publishAll();
    	sendResponse(httpResponse, httpResponse.OK, 'text/plain', " All projects have been published successfully.");
	}
}

function handleNotAllowedRequest(httpResponse) {
    sendResponse(httpResponse, httpResponse.METHOD_NOT_ALLOWED);
}

function getRequestBody(httpRequest) {
    try {
        return JSON.parse(httpRequest.readInputText());
    } catch (e) {
        return null;
    }
}

function sendResponse(response, status, contentType, content) {
    response.setStatus(status);
    response.setContentType(contentType);
    response.println(content);
    response.flush();
    response.close();   
}

