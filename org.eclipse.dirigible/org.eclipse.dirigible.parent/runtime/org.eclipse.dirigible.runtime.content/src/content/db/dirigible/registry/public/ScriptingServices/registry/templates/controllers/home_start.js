/*globals angular menuControllers*/
/*eslint-env browser */

menuControllers.controller('HomeCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.descriptionInfoItems = [];
		$scope.homeData = [];

		loadDescriptions();

		function loadDescriptions() {
			$http.get('../../js/registry/data/home/data.js').success(function(data){
				for (var i = 0 ; i < data.length; i++) {
					$scope.descriptionInfoItems.push(data[i].data);
				}
			});

		};

