/*globals controllers */
/*eslint-env browser */

controllers.controller('FlowCtrl', function($scope, $resource) {
  $scope.restService = $resource('../../flow/flow');
});