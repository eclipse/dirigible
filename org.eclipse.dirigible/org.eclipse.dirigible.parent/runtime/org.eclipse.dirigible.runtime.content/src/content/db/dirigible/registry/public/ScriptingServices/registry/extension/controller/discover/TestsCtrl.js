/*globals controllers */
/*eslint-env browser */

controllers.controller('TestsCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/tests');
});