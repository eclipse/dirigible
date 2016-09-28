/*globals controllers */

controllers.controller('WebContentCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../web');
});