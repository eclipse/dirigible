/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'hourglass-o',
		color: 'orange',
		path: '#/monitoring/response',
		title: 'Response',
		description: 'Response time statistics'
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/response',
		'template': 'templates/monitoring/response.html'
	};
};

exports.getOrder = function() {
	return 3;
};