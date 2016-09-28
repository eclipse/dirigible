/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-search",
		"title": "Discover",
		"content": "Browse the available source content of the artifacts in the Registry or search for services endpoints in a live Eclipse Dirigible instance."
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
