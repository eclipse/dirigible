/*globals controllers */

controllers.controller('MobileCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../mobile');
});