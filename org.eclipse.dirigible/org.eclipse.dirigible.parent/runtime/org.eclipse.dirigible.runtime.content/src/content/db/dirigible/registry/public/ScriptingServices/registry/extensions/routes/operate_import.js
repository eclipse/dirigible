/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/content/project',
		'controller': 'ProjectCtrl',
		'templateUrl': 'templates/content/import/import.html'
	}];
	return routes;
};
