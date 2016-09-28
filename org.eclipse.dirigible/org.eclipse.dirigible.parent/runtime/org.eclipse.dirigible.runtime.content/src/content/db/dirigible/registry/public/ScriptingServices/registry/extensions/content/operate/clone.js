/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-toggle-on",
		"title": " Clone Instances",
		"content": "Clone the whole Repository content including the Users Workspaces and Configurations. Clone Import service provide the end-point for importing cloned content from another instance. Clone Export service helps in exporting the content of the whole repository as a zip file ready for further import."
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
