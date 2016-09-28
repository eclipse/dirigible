/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/integration/job',
		'controller': 'JobCtrl',
		'templateUrl': 'templates/integration/jobs/jobs.html'
	}];
	return routes;
};
