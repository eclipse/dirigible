/*globals controllers */
/*eslint-env browser */

controllers.controller('ConfigureCtrl', ['$scope', '$http',
	function($scope, $http) {
		var accessLogUrl = "../../acclog";
		$scope.locations = null;
		$scope.newLocation;

		loadData();

		function loadData() {
			$http.get(accessLogUrl + "/locations").success(function(result) {
				$scope.locations = result;
			}).error(function() {
				alert('Could not fetch access log data!');
			});
		}

		$scope.remove = function(removeLocation) {
			$http.delete(accessLogUrl + removeLocation).success(function() {
				loadData();
			}).error(function() {
				alert('Error while removing location!');
			});
		};

		$scope.addNewLocation = function() {
			$http.post(accessLogUrl + $scope.newLocation).success(function() {
				loadData();
			}).error(function() {
				alert('Unable to add location ' + '"' + $scope.newLocation + '"'
				+ '\nLocation must be in "project/index.html" format!');
      });
    };
  }
]);
