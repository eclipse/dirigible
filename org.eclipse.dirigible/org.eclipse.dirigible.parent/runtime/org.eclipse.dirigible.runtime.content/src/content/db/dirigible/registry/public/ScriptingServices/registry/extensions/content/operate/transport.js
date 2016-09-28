/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-truck",
		"title": "Transport Content",
		"content": "Transport the public artifacts from the Registry. Import Content service provide the end-point for importing public registry content from another instance. Export Content service helps in exporting the content of the public registry as a zip file ready for further import."
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
