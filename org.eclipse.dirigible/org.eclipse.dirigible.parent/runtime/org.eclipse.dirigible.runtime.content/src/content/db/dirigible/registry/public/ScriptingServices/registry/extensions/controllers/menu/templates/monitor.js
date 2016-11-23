/*globals menuControllers */
/*eslint-env browser */

menuControllers.controller('MonitoringCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.descriptionInfoItems = [];
		$scope.monitoringData = [{
			image: 'wrench',
			color: 'blue',
			path: '#/monitoring/manage',
			title: 'Configure',
			description: 'Configure locations'
		}, {
			image: 'bar-chart',
			color: 'green',
			path: '#/monitoring/hits',
			title: 'Hits',
			description: 'Hit count statistics'
		}, {
			image: 'hourglass-o',
			color: 'orange',
			path: '#/monitoring/response',
			title: 'Response',
			description: 'Response time statistics'
		}, {
			image: 'line-chart',
			color: 'red',
			path: '#/monitoring/memory',
			title: 'Memory',
			description: 'Memory statistics'
		}, {
			image: 'ticket',
			color: 'purple',
			path: '#/monitoring/acclog',
			title: 'Access Log',
			description: 'Access Log'
		}, {
			image: 'search',
			color: 'lblue',
			path: '#/monitoring/logging',
			title: 'Apps Log',
			description: 'Applications Log'
		}, {  
		   image:'film',
		   color:'lila',
		   path:'#/monitoring/log-console',
		   title:'Log Console',
		   description:'Real-time Logs'
		}];

		loadDescriptions();
	
		function loadDescriptions() {
			$http.get('../../js/registry/data/monitor/data.js').success(function(data){
				for (var i = 0 ; i < data.length; i++) {
					$scope.descriptionInfoItems.push(data[i].data);
				}
			});
		}
	}
]);
