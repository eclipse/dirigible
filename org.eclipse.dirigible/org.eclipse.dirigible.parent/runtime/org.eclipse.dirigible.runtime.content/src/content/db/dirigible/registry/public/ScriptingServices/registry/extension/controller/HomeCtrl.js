controllers.controller('HomeCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_ITEMS = '../../js/registry/api/home/items.js';
		const API_DESCRIPTIONS = '../../js/registry/api/home/descriptions.js';

		$http.get(API_ITEMS).success(function (data) {
			$scope.homeData = data;
		});

		$http.get(API_DESCRIPTIONS).success(function (data) {
			$scope.descriptions = data;
		});
	}
]);
