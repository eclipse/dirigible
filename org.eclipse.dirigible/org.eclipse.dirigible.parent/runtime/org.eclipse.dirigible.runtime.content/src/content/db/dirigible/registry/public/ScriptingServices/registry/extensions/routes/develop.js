/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/develop',
		'controller': 'DevelopCtrl',
		'templateUrl': 'templates/develop/develop.html'
	}];
	return routes;
};
