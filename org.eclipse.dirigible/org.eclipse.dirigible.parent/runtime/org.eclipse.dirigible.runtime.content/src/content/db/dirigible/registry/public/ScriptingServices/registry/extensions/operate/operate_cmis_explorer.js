/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "book",
		color: 'red',
		path: "#/content/cmis",
		title: "Documents",
		description: "Manage Documents"
	};
	return item;
};

exports.getOrder = function() {
	return 4;
};
