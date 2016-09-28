/*globals controllers */

controllers.controller('WebWikiCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../wiki');
});