/* eslint-env node, dirigible */

exports.getType = function() {
	return 'Monitoring';
};

exports.getHomeItem = function() {
	return {  
	   image:'search',
	   color:'lblue',
	   path:'#/monitoring/log-console',
	   title:'Console',
	   description:'Real-time Logs'
	};
};

exports.getRoute = function() {
	return {
		'location': '/monitoring/log-console',
		'template': 'templates/monitoring/logconsole.html'
	};
};

exports.getOrder = function() {
	return 7;
};