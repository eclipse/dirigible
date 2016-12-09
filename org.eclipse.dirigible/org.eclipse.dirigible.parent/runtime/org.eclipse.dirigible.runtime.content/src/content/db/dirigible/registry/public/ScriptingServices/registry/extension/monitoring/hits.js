/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'bar-chart',
		color: 'green',
		path: '#/monitoring/hits',
		title: 'Hits',
		description: 'Hit count statistics'
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/hits',
		'template': 'templates/monitoring/hits.html'
	};
};

exports.getOrder = function() {
	return 2;
};