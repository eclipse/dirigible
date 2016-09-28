/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/web/wiki',
		'controller': 'WebWikiCtrl',
		'templateUrl': 'templates/web/wiki/wiki.html'
	}];
	return routes;
};
