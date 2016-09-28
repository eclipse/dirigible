/*globals menuControllers */
/*eslint-env browser */

menuControllers.controller('DiscoverCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.descriptionInfoItems = [];
		$scope.discoverData = [];

		loadDescriptions();

		function loadDescriptions() {
			$http.get('../../js/registry/data/discover/data.js').success(function(data){
				for (var i = 0 ; i < data.length; i++) {
					$scope.descriptionInfoItems.push(data[i].data);
				}
			});
		}
