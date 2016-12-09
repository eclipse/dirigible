/*globals controllers */
/*eslint-env browser */

controllers.controller('MobileCtrl', function($scope, $resource) {
  $scope.objectContent = true;
  $scope.restService = $resource('../../mobile');
});
