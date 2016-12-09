/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Develop';
};

exports.getHomeItem = function() {
	return {
		image: "desktop",
		color: "lila",
		path: "http://download.eclipse.org/dirigible/drops/M20160119-1919/p2/rcp/",
		title: "Desktop IDE",
		description: "Eclipse Plugins",
		newTab: true
	};
};

exports.getDescription = function() {
	return {
		'icon': 'fa-desktop' ,
		'title': 'Desktop Eclipse' ,
		'content': 'There is an update site with Eclipse based plugins convenient especially for \'Java-saurus\' developers, who will never go to a Web IDE for theirs daily tasks.'
	};
};

exports.getOrder = function() {
	return 3;
};
