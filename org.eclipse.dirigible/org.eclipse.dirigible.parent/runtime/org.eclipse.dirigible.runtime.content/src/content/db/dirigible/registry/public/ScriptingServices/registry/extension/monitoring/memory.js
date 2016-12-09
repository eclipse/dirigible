/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'line-chart',
		color: 'red',
		path: '#/monitoring/memory',
		title: 'Memory',
		description: 'Memory statistics'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-bar-chart-o",
		"title": "Statistics",
		"content": "Monitor the basic statistics such as Hit Count and Response time for the registered locations. Used Memory chart can give an overview about the load of the instance."
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/memory',
		'template': 'templates/monitoring/memory.html'
	};
};

exports.getOrder = function() {
	return 4;
};
