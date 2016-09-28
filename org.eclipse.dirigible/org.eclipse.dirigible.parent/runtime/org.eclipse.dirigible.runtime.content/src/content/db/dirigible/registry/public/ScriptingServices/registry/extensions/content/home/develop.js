/* globals $ */
/* eslint-env node, dirigible */

exports.getItem = function() {
	var item = {
		"icon": "fa-edit",
		"title": "Develop",
		"content": "Eclipse Dirigible provides three major types of toolkits covering the Development phase of your solution:",
		"listItems": [{
			"url": "../index.html",
			"title": "WebIDE",
			"description": " - fully functional yet powerful, browser based IDE"
		}, {
			"url": "#/workspace",
			"title": "LightIDE",
			"description": " - limited code-editing only, browser based IDE"
		}, {
			"description": "DesktopIDE - Eclipse based desktop IDE"
		}]
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};