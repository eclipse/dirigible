/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = '/${packageName}/extension_point/cockpit';

var extensions = require('core/extensions');

exports.getMenuItems = function() {
	var menu = [];
	var cockpitExtensions = getCockpitExtensions();
	for (var i = 0; i < cockpitExtensions.length; i ++) {
		if (isFunction(cockpitExtensions[i].getMenuItem)) {
			menu.push(cockpitExtensions[i].getMenuItem());
		}
	}
	return menu;
};

exports.getSidebarItems = function() {
	var sidebarData = [];
	var cockpitExtensions = getCockpitExtensions();
	for (var i = 0; i < cockpitExtensions.length; i ++) {
		if (isFunction(cockpitExtensions[i].getSidebarItem)) {
			sidebarData.push(cockpitExtensions[i].getSidebarItem());
		}
	}
	return sidebarData;
};

function getCockpitExtensions() {
	var cockpitExtensions = [];
	var extensionNames = extensions.getExtensions(EXT_POINT_NAME);
	for (var i = 0; i < extensionNames.length; i ++) {
		var extension = extensions.getExtension(extensionNames[i], EXT_POINT_NAME);
		cockpitExtensions.push(require(extension.getLocation()));
	}
	return cockpitExtensions;
}

function isFunction(f) {
	return typeof f === 'function';
}
