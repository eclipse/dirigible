/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-wrench",
		"title": "Operate",
		"content": "Perform the life-cycle management operations on a live Eclipse Dirigible instance such as Import, Export, Backup and configurations."
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
