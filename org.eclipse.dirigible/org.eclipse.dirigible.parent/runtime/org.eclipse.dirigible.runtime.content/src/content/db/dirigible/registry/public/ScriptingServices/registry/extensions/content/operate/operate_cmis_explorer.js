/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-book",
		"title": "Upload and Download Documents",
		"content": "Browse a document repository, upload and download documents, create folders, etc."
	};
	return item;
};

exports.getOrder = function() {
	return 4;
};
