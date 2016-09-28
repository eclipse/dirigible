/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "sign-in",
		color: 'yellow',
		path: "#/content/project",
		title: "Import",
		description: "Import Project"
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};
