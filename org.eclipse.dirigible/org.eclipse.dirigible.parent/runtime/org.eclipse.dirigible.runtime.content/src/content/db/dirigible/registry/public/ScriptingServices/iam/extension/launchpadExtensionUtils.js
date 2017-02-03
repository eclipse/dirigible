/* globals $ */
/* eslint-env node, dirigible */

const EXT_POINT_NAME = '/iam/extension_point/launchpad';

var extensions = require('core/extensions');

exports.getMenuItems = function() {
	var menu = [];
	var launchpadExtensions = getLaunchpadExtensions();
	for (var i = 0; i < launchpadExtensions.length; i ++) {
		if (isFunction(launchpadExtensions[i].getMenuItem)) {
			menu.push(launchpadExtensions[i].getMenuItem());
		}
	}
	return menu;
};

exports.getHomeItems = function() {
	var homeData = [];
	var launchpadExtensions = getLaunchpadExtensions();
	for (var i = 0; i < launchpadExtensions.length; i ++) {
		if (isFunction(launchpadExtensions[i].getHomeItem)) {
			homeData.push(launchpadExtensions[i].getHomeItem());
		}
	}
	return homeData;
};

function getLaunchpadExtensions() {
	var launchpadExtensions = [];
	var extensionNames = extensions.getExtensions(EXT_POINT_NAME);
	for (var i = 0; i < extensionNames.length; i ++) {
		var extension = extensions.getExtension(extensionNames[i], EXT_POINT_NAME);
		launchpadExtensions.push(require(extension.getLocation()));
	}
	return launchpadExtensions;
}

function isFunction(f) {
	return typeof f === 'function';
}
