/* globals $ */
/* eslint-env node, dirigible */

processRequest();

function processRequest() {
	var data = getData();
	sendResponse(JSON.stringify(data), "application/json");
}

function getData() {
	var homeContentExtensions = require("registry/extensions/operateContentExtensions.js");
	return homeContentExtensions.getData();
}

function sendResponse(content, contentType) {
	var response = require("net/http/response");

	response.setContentType(contentType);
	response.print(content);
	response.flush();
	response.close();	
}
