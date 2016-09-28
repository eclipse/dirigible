/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "file-code-o",
		color: 'lblue',
		path: "#/scripting/javascript",
		title: "JavaScript",
		description: "JavaScript Services"
	};
	return item;
};

exports.getOrder = function() {
	return 4;
};
