/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "area-chart",
		color: 'red',
		path: "#/monitoring",
		title: "Monitor",
		description: "Basic Metrics"
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};