/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/content/clone',
		'controller': 'CloneCtrl',
		'templateUrl': 'templates/content/import/import.html'
	}];
	return routes;
};
