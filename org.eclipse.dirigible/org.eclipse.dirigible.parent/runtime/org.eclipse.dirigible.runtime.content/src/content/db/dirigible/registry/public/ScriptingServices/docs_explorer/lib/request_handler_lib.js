/* globals $ */
/* eslint-env node, dirigible */

var request = require("net/http/request");
var response = require("net/http/response");

exports.handleRequest = function(options){
	typeof options.onBeforeHandle === "function" ? options.onBeforeHandle() : defaultOnBeforeHandle();
	
	var method = request.getMethod().toUpperCase();
	try{
		executeMethod(method, options);
	} catch(e){
		typeof options.onError === "function" ? options.onError(e) : defaultOnError(e);
	}
	
	if (typeof options.onAfterHandle === "function") { 
		options.onAfterHandle();
	}

	response.flush();
	response.close();
};

function executeMethod(method, options){
	var handler = options.handlers[method];
	if (handler){
		handler();
	} else {
		var methods = Object.keys(options.handlers);
		printError(response.BAD_REQUEST, 4, "Invalid HTTP Method. Allowed methods: " + methods, method);	
	}
}

function printError(httpCode, errCode, errMessage, errContext) {
    var body = {'err': {'code': errCode, 'message': errMessage}};
    response.setStatus(httpCode);
    response.print(JSON.stringify(body));
    console.error(JSON.stringify(body));
    if (errContext !== null) {
    	console.error(JSON.stringify(errContext));
    }
}

function defaultOnError(error){
	console.error(error);		
	printError(response.INTERNAL_SERVER_ERROR, 5, error.message);	
}

function defaultOnBeforeHandle(){
	response.setContentType("application/json");
	response.setCharacterEncoding("UTF-8");
}

