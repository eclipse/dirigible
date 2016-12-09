/*globals controllers */
/*eslint-env browser */

controllers.controller('OperateCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_ITEMS = '../../js/registry/api/operate/items.js';
		const API_DESCRIPTIONS = '../../js/registry/api/operate/descriptions.js';

		$http.get(API_ITEMS).success(function (data) {
			$scope.operateData = data;
		});

		$http.get(API_DESCRIPTIONS).success(function (data) {
			$scope.descriptions = data;
		});
	}
]);
