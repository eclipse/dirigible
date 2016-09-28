/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-film",
		"title": "Logs",
		"content": "Inspect the applications logs directly from the specified server location."
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
