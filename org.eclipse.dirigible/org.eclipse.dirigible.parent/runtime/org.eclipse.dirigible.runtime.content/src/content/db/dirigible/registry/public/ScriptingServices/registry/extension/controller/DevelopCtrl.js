/*globals controllers */
/*eslint-env browser */

controllers.controller('DevelopCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_ITEMS = '../../js/registry/api/develop/items.js';
		const API_DESCRIPTIONS = '../../js/registry/api/develop/descriptions.js';

		$http.get(API_ITEMS).success(function (data) {
			$scope.developData = data;
		});

		$http.get(API_DESCRIPTIONS).success(function (data) {
			$scope.descriptions = data;
		});
	}
]);
