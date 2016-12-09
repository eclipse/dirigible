/*globals controllers */
/*eslint-env browser */

controllers.controller('CommandCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/command');
});