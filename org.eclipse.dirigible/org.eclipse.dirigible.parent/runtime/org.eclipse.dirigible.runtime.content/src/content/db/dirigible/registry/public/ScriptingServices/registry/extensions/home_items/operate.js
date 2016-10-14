/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "wrench",
		color: 'orange',
		path: "#/operate",
		title: "Operate",
		description: "Lifecycle Management"
	};
	return item;
};

exports.getOrder = function() {
	return 2;
};