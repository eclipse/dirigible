/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "search",
		color: 'green',
		path: "#/discover",
		title: "Discover",
		description: "Service Endpoints"
	};
	return item;
};

exports.getOrder = function() {
	return 1;
};