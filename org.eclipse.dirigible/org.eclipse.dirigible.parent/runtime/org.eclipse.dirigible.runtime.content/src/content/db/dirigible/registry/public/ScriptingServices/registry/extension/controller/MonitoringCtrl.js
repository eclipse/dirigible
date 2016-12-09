/*globals controllers */
/*eslint-env browser */

controllers.controller('MonitoringCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_ITEMS = '../../js/registry/api/monitoring/items.js';
		const API_DESCRIPTIONS = '../../js/registry/api/monitoring/descriptions.js';

		$http.get(API_ITEMS).success(function (data) {
			$scope.monitoringData = data;
		});

		$http.get(API_DESCRIPTIONS).success(function (data) {
			$scope.descriptions = data;
		});
	}
]);
