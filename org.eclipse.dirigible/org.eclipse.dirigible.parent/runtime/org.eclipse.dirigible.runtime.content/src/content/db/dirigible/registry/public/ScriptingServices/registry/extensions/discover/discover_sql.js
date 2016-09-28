/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "database",
		color: 'lblue',
		path: "#/scripting/sql",
		title: "SQL",
		description: "SQL Services"
	};
	return item;
};

exports.getOrder = function() {
	return 5;
};
