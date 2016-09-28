/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: 'edit',
		color: 'blue',
		path: '#/develop',
		title: 'Develop',
		description: "Development Toolkits",
		newTab: true
	};
	return item;
};

exports.getOrder = function() {
	return 0;
};