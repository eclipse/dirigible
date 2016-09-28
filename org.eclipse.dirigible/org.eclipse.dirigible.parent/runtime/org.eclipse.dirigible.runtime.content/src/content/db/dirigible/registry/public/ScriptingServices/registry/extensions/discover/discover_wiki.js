/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "book",
		color: 'yellow',
		path: "#/web/wiki",
		title: "Wiki",
		description: "Browse Documentation"
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
