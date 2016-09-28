/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/content/import',
		'controller': 'ImportCtrl',
		'templateUrl': 'templates/content/import/import.html'
	}];
	return routes;
};
