/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/scripting/javascript',
		'controller': 'JavaScriptCtrl',
		'templateUrl': 'templates/scripting/javascript/javascript.html'
	}];
	return routes;
};
