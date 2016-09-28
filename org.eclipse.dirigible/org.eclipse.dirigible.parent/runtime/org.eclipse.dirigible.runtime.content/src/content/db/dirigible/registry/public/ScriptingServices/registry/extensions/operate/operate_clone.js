/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "toggle-on",
		color: 'green',
		path: "#/content/clone",
		title: "Clone",
		description: "Clone Instance"
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
