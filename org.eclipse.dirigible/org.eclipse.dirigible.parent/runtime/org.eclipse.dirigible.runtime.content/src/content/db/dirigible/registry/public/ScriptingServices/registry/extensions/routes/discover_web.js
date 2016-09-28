/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/web/content',
		'controller': 'WebContentCtrl',
		'templateUrl': 'templates/web/content/content.html'
	}];
	return routes;
};
