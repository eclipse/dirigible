/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/workspace',
		'controller': 'WorkspaceCtrl',
		'templateUrl': 'templates/workspace/workspace.html'
	}];
	return routes;
};
