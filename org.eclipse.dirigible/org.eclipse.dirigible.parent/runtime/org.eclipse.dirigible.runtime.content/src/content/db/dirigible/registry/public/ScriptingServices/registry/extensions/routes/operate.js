/*eslint-env node */

exports.getRoutes = function() {
	var routes = [{
		'location': '/operate',
        'controller': 'OperateCtrl',
        'templateUrl': 'templates/operate/operate.html'
	}];
	return routes;
};
