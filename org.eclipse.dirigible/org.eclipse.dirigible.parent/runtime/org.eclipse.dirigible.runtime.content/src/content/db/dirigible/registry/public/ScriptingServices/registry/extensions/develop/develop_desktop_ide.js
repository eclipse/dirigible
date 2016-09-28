/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "desktop",
		color: 'lila',
		path: "http://download.eclipse.org/dirigible/drops/M20160119-1919/p2/rcp/",
		title: "Desktop IDE",
		description: "Eclipse Plugins"
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
