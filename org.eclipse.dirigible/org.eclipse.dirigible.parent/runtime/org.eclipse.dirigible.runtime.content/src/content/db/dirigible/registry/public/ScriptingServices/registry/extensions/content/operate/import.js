/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-sign-in",
		"title": "Import Projects",
		"content": "Import Project service provide the end-point for importing project content in design time format (source). This is useful for constructing a PROD instance (consisting only of Runtime components) by importing one or many ready to use source projects."
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
