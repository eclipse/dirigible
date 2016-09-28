/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/content',
		'controller': 'ContentCtrl',
		'templateUrl': 'templates/content/content.html'
	}];
	return routes;
};
