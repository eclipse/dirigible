/*globals controllers */
/*eslint-env browser */

controllers.controller('WebWikiCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../wiki');
});
