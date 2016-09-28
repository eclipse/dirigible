/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "caret-square-o-right",
		color: 'orange',
		path: "#/integration/listener",
		title: "Listeners",
		description: "Integration Listeners"
	};
	return item;
};

exports.getOrder = function() {
	return 10;
};
