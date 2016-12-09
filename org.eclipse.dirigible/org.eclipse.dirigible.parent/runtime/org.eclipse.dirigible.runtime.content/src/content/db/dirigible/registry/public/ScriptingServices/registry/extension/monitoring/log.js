/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {
		image: 'film',
		color: 'lblue',
		path: '#/monitoring/logging',
		title: 'Log',
		description: 'Applications Log'
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/logging',
		'template': 'templates/monitoring/log.html'
	};
};

exports.getDescription = function() {
	return {
		"icon": "fa-film",
		"title": "Logs",
		"content": "Inspect the applications logs directly from the specified server location."
	};

};

exports.getOrder = function() {
	return 6;
};
