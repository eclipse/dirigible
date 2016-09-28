/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = "/registry/extension_points/develop_content_item";

var extensionService = $.getExtensionService();

exports.getData = function() {
	var extensions = extensionService.getExtensions(EXT_POINT_NAME);
	var data = [];
	for (var i = 0; i < extensions.length; i++) {
	    var extension = require(extensions[i]);
	    data.push(createItem(extension));
	}
	sort(data);
	return data;
};

function createItem(extension) {
	var item =  {
		"data": extension.getItem(),
		"order": extension.getOrder()
	};
	return item;
}

function sort(data) {
	data.sort(function(a, b) {
		return a.order - b.order;
	});
}
