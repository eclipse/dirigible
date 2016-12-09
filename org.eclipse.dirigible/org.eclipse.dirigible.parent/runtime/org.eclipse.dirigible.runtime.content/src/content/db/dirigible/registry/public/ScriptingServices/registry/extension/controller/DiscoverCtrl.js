/*globals controllers */
/*eslint-env browser */

controllers.controller('DiscoverCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_ITEMS = '../../js/registry/api/discover/items.js';
		const API_DESCRIPTIONS = '../../js/registry/api/discover/descriptions.js';

		$http.get(API_ITEMS).success(function (data) {
			$scope.discoverData = data;
		});

		$http.get(API_DESCRIPTIONS).success(function (data) {
			$scope.descriptions = data;
		});
	}
]);
