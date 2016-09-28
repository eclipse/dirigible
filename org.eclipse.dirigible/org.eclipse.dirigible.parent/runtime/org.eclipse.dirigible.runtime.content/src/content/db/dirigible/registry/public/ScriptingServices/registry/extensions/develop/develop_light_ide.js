/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "mobile",
		color: 'lblue',
		path: "#/workspace",
		title: "Light IDE",
		description: "Lightweight Development"
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};
