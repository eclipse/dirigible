var response = require('http/v3/response');

// HTTP 200
exports.sendResponseOk = function(entity) {
	this.sendResponse(200, entity);
};

// HTTP 201
exports.sendResponseCreated = function(entity) {
	this.sendResponse(201, entity);
};

// HTTP 200
exports.sendResponseNoContent = function() {
	this.sendResponse(204);
};

// HTTP 400
exports.sendResponseBadRequest = function(message) {
	this.sendResponse(404, {
		'code': 400,
		'message': message
	});
};

// HTTP 404
exports.sendResponseNotFound = function(message) {
	this.sendResponse(404, {
		'code': 404,
		'message': message
	});
};

// Generic
exports.sendResponse = function(status, body) {
	response.setContentType('application/json');
	response.setStatus(status);
	if (body) {
		response.println(JSON.stringify(body));
	}
};