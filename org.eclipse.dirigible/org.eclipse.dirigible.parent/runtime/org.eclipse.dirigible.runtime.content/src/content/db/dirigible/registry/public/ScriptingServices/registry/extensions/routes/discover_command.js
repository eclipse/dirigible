/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/scripting/command',
		'controller': 'CommandCtrl',
		'templateUrl': 'templates/scripting/command/command.html'
	}];
	return routes;
};
