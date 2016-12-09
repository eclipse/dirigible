/*globals controllers */
/*eslint-env browser */

controllers.controller('JavaScriptCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../scripting/javascript');
});