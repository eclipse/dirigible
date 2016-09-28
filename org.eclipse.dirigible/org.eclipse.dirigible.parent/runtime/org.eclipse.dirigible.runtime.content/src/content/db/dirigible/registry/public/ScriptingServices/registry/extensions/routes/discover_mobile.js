/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/mobile',
		'controller': 'MobileCtrl',
		'templateUrl': 'templates/mobile/mobile.html'
	}];
	return routes;
};
