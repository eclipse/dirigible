/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "deaf",
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
