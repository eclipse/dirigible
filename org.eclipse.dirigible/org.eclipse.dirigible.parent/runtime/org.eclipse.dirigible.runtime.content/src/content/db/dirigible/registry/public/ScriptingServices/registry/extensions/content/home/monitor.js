/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-area-chart",
		"title": "Monitor",
		"content": "Monitor the basic metrics of a live Eclipse Dirigible instance as well as inspect the applications and audit logs."
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
