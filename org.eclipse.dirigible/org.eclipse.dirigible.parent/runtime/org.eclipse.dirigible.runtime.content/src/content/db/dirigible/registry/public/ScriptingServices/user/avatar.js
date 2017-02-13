/* globals $ */
/* eslint-env node, dirigible */

var documentLib = require("docs_explorer/lib/document_lib");
var requestHandler = require("docs_explorer/lib/request_handler_lib");
var request = require("net/http/request");
var response = require("net/http/response");
var user = require('net/http/user');
var xss = require('utils/xss');
var repository = require('platform/repository');


requestHandler.handleRequest({
	handlers : {
		GET: handleGet
	},
});

function handleGet() {
	var pathPrefix = "/user/avatar.js/";
	var userName = xss.escapeSql(request.getInfo().pathInfo);
	userName = userName.substring(pathPrefix.length);
	if (userName === "") {
		userName = user.getName();
	}
	var documentPath = "/iam/users/" + userName + "/avatar.png";
	try {
		var document = documentLib.getDocument(documentPath);
		var contentStream = documentLib.getDocumentStream(document);
		var contentType = contentStream.getInternalObject().getMimeType();
		response.setContentType(contentType);
		response.writeStream(contentStream.getStream());
	} catch(e) {
		// asuming the avatar does not exist
		var defaultAvatar = repository.getResource("/db/dirigible/registry/public/WebContent/profile/default.png");
		response.setContentType("image/png");
		response.writeOutput(defaultAvatar.getContent());
	}
	response.flush();
	response.close();
}
