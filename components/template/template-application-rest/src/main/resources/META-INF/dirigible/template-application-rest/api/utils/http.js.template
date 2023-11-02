var response = require("http/response");

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
		"code": 400,
		"message": message
	});
};

// HTTP 403
exports.sendForbiddenRequest = function(message) {
	this.sendResponse(403, {
		"code": 403,
		"message": message
	});
};

// HTTP 404
exports.sendResponseNotFound = function(message) {
	this.sendResponse(404, {
		"code": 404,
		"message": message
	});
};

// HTTP 500
exports.sendInternalServerError = function(message) {
	this.sendResponse(500, {
		"code": 500,
		"message": message
	});
};

// Generic
exports.sendResponse = function(status, body) {
	response.setContentType("application/json");
	response.setStatus(status);
	if (body) {
		response.println(JSON.stringify(body));
	}
};