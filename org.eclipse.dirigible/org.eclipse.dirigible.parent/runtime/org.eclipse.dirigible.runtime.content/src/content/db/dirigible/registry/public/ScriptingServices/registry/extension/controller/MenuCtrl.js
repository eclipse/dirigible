controllers.controller('MenuCtrl', ['$scope', '$http',
	function($scope, $http) {
		const API_MENU = '../../js/registry/api/menu.js';

		$http.get(API_MENU).success(function (data) {
			$scope.menu = data;
		});
	}
]);
