/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/scripting/sql',
		'controller': 'SQLCtrl',
		'templateUrl': 'templates/scripting/sql/sql.html'
	}];
	return routes;
};
