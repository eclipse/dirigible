/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "truck",
		color: 'blue',
		path: "#/content/import",
		title: "Transport",
		description: "Transport Registry Content"
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};
