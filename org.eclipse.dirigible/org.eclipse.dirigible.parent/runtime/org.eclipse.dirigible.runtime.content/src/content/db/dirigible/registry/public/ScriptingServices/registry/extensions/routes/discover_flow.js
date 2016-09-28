/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/integration/flow',
		'controller': 'FlowCtrl',
		'templateUrl': 'templates/integration/flows/flows.html'
	}];
	return routes;
};
