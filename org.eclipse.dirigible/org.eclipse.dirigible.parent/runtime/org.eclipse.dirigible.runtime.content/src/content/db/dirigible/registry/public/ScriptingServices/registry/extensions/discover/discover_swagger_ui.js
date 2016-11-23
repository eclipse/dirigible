/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: 'wrench',
		color: 'green',
		path: '../swagger_ui/index.html',
		title: 'Swagger',
		description: "Swagger UI",
		newTab: true
	};
	return item;
};

exports.getOrder = function() {
	return 12;
};
