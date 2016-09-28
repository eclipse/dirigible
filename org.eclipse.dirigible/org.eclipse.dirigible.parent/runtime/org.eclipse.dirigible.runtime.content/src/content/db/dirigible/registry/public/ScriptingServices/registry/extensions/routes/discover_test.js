/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/scripting/tests',
		'controller': 'TestsCtrl',
		'templateUrl': 'templates/scripting/tests/tests.html'
	}];
	return routes;
};
