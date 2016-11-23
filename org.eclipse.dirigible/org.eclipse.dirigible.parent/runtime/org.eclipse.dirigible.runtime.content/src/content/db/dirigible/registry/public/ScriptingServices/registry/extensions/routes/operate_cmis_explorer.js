/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/content/cmis',
		'controller': 'explorerCtrl',
		'templateUrl': 'templates/content/cmis/cmis.html'
	}];
	return routes;
};
