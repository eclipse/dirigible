/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/integration/listener',
		'controller': 'ListenerCtrl',
		'templateUrl': 'templates/integration/listeners/listeners.html'
	}];
	return routes;
};
