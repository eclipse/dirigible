/* globals $ */
/* eslint-env node, dirigible */

var response = require("net/http/response");
var request = require("net/http/request");
var URI = require("lib/URI");

var findInAcceptHeader = function(httpReqHeaderAccept, mime){
	return httpReqHeaderAccept.filter(function(entry){
		return entry.split(';')[0].trim() === mime;
	})[0] !== undefined;
};

var endResponse = function(){
	response.flush();
	response.close();
};	

exports.service = function(settings) {

	var httpReqHeaderAccept = request.getHeader("Accept").replace(/\\/g,'').split(',');		
	response.setContentType("application/json; charset=UTF-8");
	response.setCharacterEncoding("UTF-8");
	
	if(findInAcceptHeader(httpReqHeaderAccept, 'text/html')){
		console.info('[Test Runner SVC] Handling request for HTML test run results');
		
		var requestUrl = URI($.getRequest().getRequestURL()).normalizePath().path();
		console.info('[Test Runner SVC] redirecting to /services/web/test/tests_dashboard.html with URL query string rewrite to url='+requestUrl);
		response.addHeader('Location', '/services/web/tests/tests_dashboard.html?url='+requestUrl);
		response.setStatus(response.FOUND);
		
	} else if (findInAcceptHeader(httpReqHeaderAccept, 'application/xml') || findInAcceptHeader(httpReqHeaderAccept, 'text/xml') || findInAcceptHeader(httpReqHeaderAccept, '*/xml')){
		console.info('[Test Runner SVC] Handling request for JSON test run results');

		settings.serviceReporter.forMedia("xml");	
		settings.execute();
		
	}	else if (findInAcceptHeader(httpReqHeaderAccept, 'application/json') || findInAcceptHeader(httpReqHeaderAccept, 'text/json') || findInAcceptHeader(httpReqHeaderAccept, '*/json') || findInAcceptHeader(httpReqHeaderAccept, '*/*')){
		console.info('[Test Runner SVC] Handling request for JSON test run results');

		settings.serviceReporter.forMedia("json");	
		settings.execute();
	}	

	endResponse();
	
};
