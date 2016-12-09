/*eslint-env node */

exports.getType = function() {
	return 'Discover';
};

exports.getHomeItem = function() {
	return {
		image: 'wrench',
		color: 'green',
		path: '../swagger_ui/index.html',
		title: 'Swagger',
		description: "Swagger UI",
		newTab: true
	};
};

exports.getOrder = function() {
	return 12;
};