/* globals $, java */
/* eslint-env node, dirigible */

exports.getContent = function(fileName) {
	var file = $.getRepository().getResource(fileName);
	return new java.lang.String(file.getContent());
};
