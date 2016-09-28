/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "gear",
		color: 'lblue',
		path: "#/scripting/command",
		title: "Command",
		description: "Command Services"
	};
	return item;
};

exports.getOrder = function() {
	return 6;
};
