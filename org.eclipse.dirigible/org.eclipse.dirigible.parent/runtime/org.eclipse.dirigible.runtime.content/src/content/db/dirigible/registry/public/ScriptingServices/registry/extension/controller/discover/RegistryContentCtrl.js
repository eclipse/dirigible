/*globals controllers */
/*eslint-env browser */

controllers.controller('RegistryContentCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../content');
});