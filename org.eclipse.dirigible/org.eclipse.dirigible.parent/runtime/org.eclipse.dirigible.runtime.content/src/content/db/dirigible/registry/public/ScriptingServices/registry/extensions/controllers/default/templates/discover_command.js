/*globals controllers */

controllers.controller('CommandCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/command');
});