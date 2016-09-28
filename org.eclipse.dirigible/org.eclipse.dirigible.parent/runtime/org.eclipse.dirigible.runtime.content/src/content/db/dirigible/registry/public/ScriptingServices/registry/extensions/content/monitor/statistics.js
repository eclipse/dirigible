/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-bar-chart-o",
		"title": "Statistics",
		"content": "Monitor the basic statistics such as Hit Count and Response time for the registered locations. Used Memory chart can give an overview about the load of the instance."
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
