/*globals controllers */

controllers.controller('TestsCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/tests');
});