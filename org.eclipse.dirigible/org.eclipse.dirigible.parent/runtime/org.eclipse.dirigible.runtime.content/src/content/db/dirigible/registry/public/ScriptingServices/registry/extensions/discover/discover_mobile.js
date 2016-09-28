/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "mobile-phone",
		color: 'blue',
		path: "#/mobile",
		title: "Mobile",
		description: "Native Mobile Apps"
	};
	return item;
};

exports.getOrder = function() {
	return 3;
};
