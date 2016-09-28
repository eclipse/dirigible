/*globals angular menuControllers*/
/*eslint-env browser */

menuControllers.controller('DevelopCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.descriptionInfoItems = [];
		$scope.developData = [];

		loadDescriptions();

		function loadDescriptions() {
			$http.get('../../js/registry/data/develop/data.js').success(function(data){
				for (var i = 0 ; i < data.length; i++) {
					$scope.descriptionInfoItems.push(data[i].data);
				}
			});
		}

