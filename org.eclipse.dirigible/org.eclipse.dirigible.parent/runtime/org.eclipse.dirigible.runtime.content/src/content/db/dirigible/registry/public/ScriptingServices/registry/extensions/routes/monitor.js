/*eslint-env node */


exports.getRoutes = function() {
	var routes = [{
		'location': '/monitoring',
        'controller': 'MonitoringCtrl',
        'templateUrl': 'templates/monitoring/monitoring.html'
	}, {
		'location': '/monitoring/manage',
        'controller': 'MonitoringManageCtrl',
        'templateUrl': 'templates/monitoring/manage/manage.html'
	}, {
		'location': '/monitoring/hits',
        'templateUrl': 'templates/monitoring/hits/hits.html'
	}, {
		'location': '/monitoring/response',
        'templateUrl': 'templates/monitoring/response/response.html'
	}, {
		'location': '/monitoring/memory',
        'templateUrl': 'templates/monitoring/memory/memory.html'
	}, {
		'location': '/monitoring/acclog',
		'controller': 'MonitoringAccessCtrl',
        'templateUrl': 'templates/monitoring/acclog/acclog.html'
	}, {
		'location': '/monitoring/logging',
        'templateUrl': 'templates/monitoring/logging/logging.html'
	}, {
		'location': '/monitoring/log-console',
        'templateUrl': 'templates/monitoring/logging/log-console.html'
	}, {
		'location': '/monitoring/log',
        'templateUrl': 'templates/monitoring/logging/log.html'
	}];
	return routes;
};
