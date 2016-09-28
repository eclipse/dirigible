/*globals controllers */

controllers.controller('JavaScriptCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/javascript');
});