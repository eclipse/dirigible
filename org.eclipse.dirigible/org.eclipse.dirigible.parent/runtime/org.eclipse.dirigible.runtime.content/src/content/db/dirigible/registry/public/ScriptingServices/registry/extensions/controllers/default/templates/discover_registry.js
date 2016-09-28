/*globals controllers */

controllers.controller('ContentCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../content');
});