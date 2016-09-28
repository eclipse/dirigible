/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "caret-square-o-right",
		color: 'orange',
		path: "#/integration/job",
		title: "Jobs",
		description: "Integration Jobs"
	};
	return item;
};

exports.getOrder = function() {
	return 9;
};
