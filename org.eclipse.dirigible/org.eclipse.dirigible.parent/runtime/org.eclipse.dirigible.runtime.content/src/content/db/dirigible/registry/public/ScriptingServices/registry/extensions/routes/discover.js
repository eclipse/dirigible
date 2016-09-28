/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/discover',
		'controller': 'DiscoverCtrl',
		'templateUrl': 'templates/discover/discover.html'
	}];
	return routes;
};
