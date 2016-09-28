/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "caret-square-o-right",
		color: 'orange',
		path: "#/integration/flow",
		title: "Flows",
		description: "Integration Flows"
	};
	return item;
};

exports.getOrder = function() {
	return 8;
};
