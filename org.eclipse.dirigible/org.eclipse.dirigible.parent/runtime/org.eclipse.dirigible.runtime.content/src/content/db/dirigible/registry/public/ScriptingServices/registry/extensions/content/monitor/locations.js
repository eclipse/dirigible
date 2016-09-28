/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-wrench",
		"title": "Manage Location",
		"content": "Register the locations to which the statistics shall be collected."
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
