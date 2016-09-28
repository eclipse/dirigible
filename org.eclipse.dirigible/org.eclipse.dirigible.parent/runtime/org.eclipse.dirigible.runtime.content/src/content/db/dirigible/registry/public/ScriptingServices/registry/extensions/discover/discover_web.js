/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "globe",
		color: 'yellow',
		path: "#/web/content",
		title: "Web",
		description: "Browse User Interfaces"
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
