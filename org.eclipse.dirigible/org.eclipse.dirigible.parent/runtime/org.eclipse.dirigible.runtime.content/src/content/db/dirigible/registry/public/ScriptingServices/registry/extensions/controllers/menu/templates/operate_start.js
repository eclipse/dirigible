/*globals menuControllers */
/*eslint-env browser */

menuControllers.controller('OperateCtrl', ['$scope', '$http',
	function($scope, $http) {
		$scope.descriptionInfoItems = [];
		$scope.operateData = [];

		loadDescriptions();
	
		function loadDescriptions() {
			$http.get('../../js/registry/data/operate/data.js').success(function(data){
				for (var i = 0 ; i < data.length; i++) {
					$scope.descriptionInfoItems.push(data[i].data);
				}
			});
		}
